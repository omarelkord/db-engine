package app;

import antlr.AntlrToProgram;
import antlr.Program;
import gen.gLexer;
import gen.gParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class SQLExprApp {
    public static void main(String[] args){

        String fileName = "D:/db-engine/test.txt";

        gParser parser = getParser(fileName);
        ParseTree antlrAST = parser.prog();
        AntlrToProgram progVisitor = new AntlrToProgram();
        Program prog = progVisitor.visit(antlrAST);




    }

    private static gParser getParser(String fileName){

        gParser parser = null;


        try {
            CharStream input = CharStreams.fromFileName(fileName);
            gLexer lexer = new gLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new gParser(tokens);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parser;

    }
}
