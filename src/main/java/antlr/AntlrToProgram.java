package antlr;

import gen.gParser;

public class AntlrToProgram extends gen.gBaseVisitor<Program> {
    @Override
    public Program visitProg(gParser.ProgContext ctx) {
        Program prog = new Program();
        AntlrToSQLExpr exprVis = new AntlrToSQLExpr();

        prog.sqlExpr = exprVis.visit(ctx.getChild(0));
        System.out.println(prog.sqlExpr);
        return prog;
    }
}
