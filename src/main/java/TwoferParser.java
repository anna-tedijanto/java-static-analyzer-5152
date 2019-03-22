import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import static com.github.javaparser.JavaParser.*;
import java.io.File;
import com.github.javaparser.JavaParser;
import java.util.List;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.TreeVisitor;
import java.util.ArrayList;


class TwoferParser {

    public static CompilationUnit getContent(String flname) throws Exception{
        return JavaParser.parse(new File(flname));
    }

    private static class ReturnVisitor extends VoidVisitorAdapter<List<ReturnStmt>> {

        private boolean optionalUse;

        public  ReturnVisitor(boolean optionalUse){
            this.optionalUse = optionalUse;
        }

        @Override
        public void visit(ReturnStmt n, List<ReturnStmt> returnStatements) {
            System.out.println("Returning: " + n.getExpression().get().toString());
            returnStatements.add(n);
            super.visit(n, returnStatements);
        }
    }

    public static class SimpleVisitor extends TreeVisitor {

        public void process(Node node){
            System.out.println(node.getClass() + " || " + node.toString());
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        }

    }

    private static class OptionalMethodVisitor extends VoidVisitorAdapter<List<MethodCallExpr>> {
        @Override
        public void visit(MethodCallExpr n, List<MethodCallExpr> arg) {
            arg.add(n);
            super.visit(n, arg);
        }
    }

    private static boolean returnChecker(List<ReturnStmt> returns){
        if(returns.size() > 1){
            System.out.println("Can be refactored to use less return statements");
            return false;
        }
        else{
            ReturnStmt n = returns.get(0);
            List<MethodCallExpr> returnMethods = new ArrayList<MethodCallExpr>();
            n.accept(new OptionalMethodVisitor(), returnMethods);
            if(returnMethods.size() == 1){
                System.out.println("arguments: " + returnMethods.get(0).getArguments().toString());
                System.out.println(returnMethods.get(0).getNameAsString());
                if(returnMethods.get(0).getScope().get().toString().equals("String") &&
                        returnMethods.get(0).getNameAsString().equals("format")){
                    if(!returnMethods.get(0).getArgument(0).toString().equals("\"One for %s, one for me.\"")){
                        return false;
                    };
                    if(!returnMethods.get(0).getArgument(1).toString().equals("name == null ? \"you\" : name") &&
                        !returnMethods.get(0).getArgument(1).toString().equals("name != null ? name : \"you\"")) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static boolean parse(String flname) throws Exception {
        CompilationUnit cu = getContent(flname);
        List<ReturnStmt> returns = new ArrayList<ReturnStmt>();
        cu.accept(new ReturnVisitor(false), returns);

        if(returns.size() < 1){
            System.out.println("There's no return statement");
        }
        System.out.println("\n");

        System.out.println("return is correct: " + returnChecker(returns));
        return true;
    }

}
