package json.execute.vm.entity.frame;

import json.execute.entity.LocationRange;
import json.execute.entity.ast.AST;
import json.execute.entity.ast.Identifier;
import json.execute.entity.ast.represents.object.Field;
import json.execute.vm.entity.state.Type;
import json.execute.vm.entity.state.Value;
import json.execute.vm.entity.state.heap.Heap;
import json.execute.vm.entity.state.heap.HeapEntity;
import json.execute.vm.entity.state.heap.HeapObject;
import json.execute.vm.entity.state.heap.HeapThunk;

import java.util.List;
import java.util.Map;

public class Frame {

    private FrameKind kind;
    private AST ast;
    private LocationRange location;
    boolean tailCall;
    private Value val;
    private Value val2;
    private List<Field> fit;
    private Map<Identifier, json.execute.vm.entity.state.Field> objectFields;
    private int elementId;
    private Map<Identifier, HeapThunk> elements;
    private List<HeapThunk> thunks;
    private HeapEntity context;
    private HeapObject self;
    private int offset;
    private Map<Identifier, HeapThunk> bindings;

    public Frame(FrameKind kind, LocationRange location) {
        this.kind = kind;
        this.location = location;
        this.ast = null;
        this.tailCall = false;
        this.elementId = 0;
        this.context = null;
        this.self = null;
        this.offset = 0;
        this.val.setT(Type.NULL_TYPE);
        this.val2.setT(Type.NULL_TYPE);
    }

    public Frame(FrameKind kind, AST ast) {
        this.kind = kind;
        this.ast = ast;
        this.location = ast.getLocation();
        this.tailCall = false;
        this.elementId = 0;
        this.context = null;
        this.self = null;
        this.offset = 0;
        this.val.setT(Type.NULL_TYPE);
        this.val2.setT(Type.NULL_TYPE);
    }

    public void mark(Heap heap) {
        heap.markFrom(val);
        heap.markFrom(val2);
        if (context != null) heap.markFrom(context);
        if (self != null) heap.markFrom(self);
        for (Map.Entry bind : bindings.entrySet())
            heap.markFrom((HeapThunk) bind.getValue());
        for (Map.Entry el : elements.entrySet())
            heap.markFrom((HeapThunk) el.getValue());
        for (HeapThunk th : thunks)
            heap.markFrom(th);
    }

    public boolean isCall() {
        return kind == FrameKind.FRAME_CALL;
    }

}
