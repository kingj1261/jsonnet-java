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

public class Executor {

    private Lexer lexer = new Lexer();
    private Parser parser = new Parser();

    public static void main(String[] args) throws IOException {
        Executor executor = new Executor();

        String jsonnet = executor.readFile("", Charset.defaultCharset());
        String updatedJson = executor.getUpdatedJson(jsonnet);
        System.out.println(updatedJson);
    }

    public String getUpdatedJson(String jsonnet) throws IOException {
        List<Token> tokens = lexer.lexJsonnet(jsonnet);

        parser.setTokens(tokens);
        AST ast = parser.parse(Parser.MAX_PRECEDENCE, 0);

        return jsonnet;
    }

    public String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
