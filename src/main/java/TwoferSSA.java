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


class TwoferSSA {

    public static CompilationUnit getContent(String flname) throws Exception{
        return JavaParser.parse(new File(flname));
    }

    private static class TwoferVarCheck extends VoidVisitorAdapter<Void> {
        String nameVal = null;

    }

    public static String parse(String flname) throws Exception {
        CompilationUnit cu = getContent(flname);
        TwoferVarCheck tv = new TwoferVarCheck();
        cu.accept(tv, null);
        return tv.nameVal;
    }

}
