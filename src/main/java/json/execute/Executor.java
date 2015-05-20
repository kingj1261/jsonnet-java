package json.execute;

import json.execute.entity.ast.AST;
import json.execute.entity.Token;
import json.execute.lex.Lexer;
import json.execute.parser.Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Executor {


    public static void main(String[] args) throws IOException {

        String jsonnet = Executor.readFile("", Charset.defaultCharset());
        String updatedJson = Executor.getUpdatedJson(jsonnet);
        System.out.println(updatedJson);
    }

    public static String getUpdatedJson(String jsonnet) throws IOException {
        AST expr = Parser.jsonnet_parse("execute", jsonnet);

        String json_str;
        Map<String, String> files;

        String unparse = Parser.jsonnet_unparse_jsonnet(expr);

        return jsonnet;
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
