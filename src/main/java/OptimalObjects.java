package exercism_parser;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class OptimalObjects {
	CompilationUnit cu;
	
	OptimalObjects(CompilationUnit cu){
		this.cu = cu;
	}
	
    private static class OptionalMethodVisitor extends VoidVisitorAdapter<List<MethodCallExpr>> {
        @Override
        public void visit(MethodCallExpr n, List<MethodCallExpr> arg) {
            // Found a method call
//        	System.out.println("Method name");
//            System.out.println(n.getScope() + " - " + n.getName());
//            System.out.println("Arguments");
//            System.out.println(n.getArguments().toString() + "\n");
            arg.add(n);
            // Don't forget to call super, it may find more method calls inside the arguments of this method call, for example.
            super.visit(n, arg);
        }
    }
    
    public boolean parse(List<ReturnStmt> returns){
    	if(returns.size() > 1){
    		//when would this apply?
    		System.out.println("Can be refactored to use less return statements");    		
        	return false;
    	}
    	else{
    		ReturnStmt n = returns.get(0);
    		List<MethodCallExpr> returnMethods = new ArrayList<MethodCallExpr>();
    		n.accept(new OptionalMethodVisitor(), returnMethods);
    		if(returnMethods.size() == 2){
    			if(returnMethods.get(0).getScope().get().toString().equals("String") &&
    					returnMethods.get(0).getNameAsString().equals("format")){
    				if(!returnMethods.get(0).getArgument(0).toString().equals("\"One for %s, one for me.\"")){
    					return false;
    				};
    			}
    			if(returnMethods.get(1).getScope().get().toString().equals("Objects") &&
    					returnMethods.get(1).getNameAsString().equals("toString")){
    				if(!returnMethods.get(1).getArgument(0).toString().equals("name")){
    					return false;
    				};
    				if(!returnMethods.get(1).getArgument(1).toString().equals("\"you\"")){
    					return false;
    				}
    			}
    		}
    		else{
    			return false;
    		}
    		return true;
    	}
    }
}
