package json.execute.vm.entity.state.heap;

import json.execute.vm.entity.state.GarbageCollectionMark;
import json.execute.vm.entity.state.Value;

import java.util.List;
import java.util.Map;

public class Heap {

    private int gcTuneMinObjects;
    private int gcTuneGrowthTrigger;
    private GarbageCollectionMark lastMark;
    private List<HeapEntity> entities;
    private List<HeapEntity> stash;
    private long lastNumEntities;
    private long numEntities;

    public Heap(int gcTuneMinObjects, int gcTuneGrowthTrigger) {
        this.gcTuneMinObjects = gcTuneMinObjects;
        this.gcTuneGrowthTrigger = gcTuneGrowthTrigger;
        this.lastMark = new GarbageCollectionMark((char) 0);
        this.lastNumEntities = 0;
        this.numEntities = 0;
    }

    public void markFrom(Value v) {
        if (v.isHeap()) markFrom(v.getV().getH());
    }

    public void markFrom(HeapEntity from) {
        assert (from != null);
        char mark = lastMark.getMark();
        GarbageCollectionMark thisMark = new GarbageCollectionMark(++mark);
        if (from.getMark() == thisMark) return;
        from.setMark(thisMark);
        if (from instanceof HeapSimpleObject) {
            HeapSimpleObject obj = (HeapSimpleObject) from;
            for (Map.Entry upv : obj.getUpValues().entrySet())
                markFrom((HeapThunk) upv.getValue());
        } else if (from instanceof HeapExtendedObject) {
            HeapExtendedObject obj = (HeapExtendedObject) from;
            markFrom(obj.getLeft());
            markFrom(obj.getRight());
        } else if (from instanceof HeapComprehensionObject) {
            HeapComprehensionObject obj = (HeapComprehensionObject) from;
            for (Map.Entry upv : obj.getUpValues().entrySet())
                markFrom((HeapThunk) upv.getValue());
            for (Map.Entry upv : obj.getCompValues().entrySet())
                markFrom((HeapThunk) upv.getValue());
        } else if (from instanceof HeapSuperObject) {
            HeapSuperObject obj = (HeapSuperObject) from;
            markFrom(obj.getRoot());
        } else if (from instanceof HeapArray) {
            HeapArray arr = (HeapArray) from;
            for (HeapThunk el : arr.getElements())
                markFrom(el);
        } else if (from instanceof HeapClosure) {
            HeapClosure func = (HeapClosure) from;
            for (Map.Entry upv : func.getUpValues().entrySet())
                markFrom((HeapThunk) upv.getValue());
            if (func.getSelf() != null) markFrom(func.getSelf());
        } else if (from instanceof HeapThunk) {
            HeapThunk thunk = (HeapThunk) from;
            if (thunk.isFilled()) {
                if (thunk.getContent().isHeap())
                    markFrom(thunk.getContent().getV().getH());
            } else {
                for (Map.Entry upv : thunk.getUpValues().entrySet())
                    markFrom((HeapThunk) upv.getValue());
                if (thunk.getSelf() != null) markFrom(thunk.getSelf());
            }
        }
    }

    public void stashIfIsHeap(Value v) {
        if (!v.isHeap()) return;
        stash.add(v.getV().getH());
    }

    public void stashIfIsHeap(HeapEntity h) {
        stash.add(h);
    }

    public void markFromStash() {
        for (HeapEntity l : stash) {
            markFrom(l);
        }
    }

    public void sweep() {
        char mark = lastMark.getMark();
        mark++;
        lastMark.setMark(mark);
        // Heap shrinks during this loop.  Do not cache entities.size().
        for (int i = 0; i < entities.size(); ++i) {
            HeapEntity x = entities.get(i);
            if (x.getMark().getMark() != lastMark.getMark()) {
                if (i != entities.size() - 1) {
                    // Swap it with the back.
                    entities.add(i, entities.get(entities.size() - 1));
                }
                entities.remove(entities.size() - 1);
                --i;
            }
        }
        lastNumEntities = numEntities = entities.size();
    }

    public boolean checkHeap() {
        return numEntities > gcTuneMinObjects
                && numEntities > gcTuneGrowthTrigger * lastNumEntities;
    }


}
