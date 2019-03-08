package exercism_parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class Twofer_Parser {
		
    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            /* here you can access the attributes of the method.
             this method will be called for all methods in this 
             CompilationUnit, including inner class methods */
        	
            System.out.println(n.getName());
            System.out.println(n.getParameter(0));
            System.out.println(n.getType());
            if (!n.getName().asString().equals("twofer")){
            	System.out.println("Method is not named twofer");
            }
            if(n.getParameters().size() != 1){
            	System.out.println("Incorrect number of parameters");
            }
            else{
            	if(!n.getParameter(0).toString().equals("String name")){
            		System.out.println("Incorrect parameter type of name");
            	}
            }
            
            super.visit(n, arg);
        }
    }
    
    private static class ReturnVisitor extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(ReturnStmt n, List<String> returnStatements) {
            //Check what value is being returned
        	
        	System.out.println("Returning: " + n.getExpression().get().toString());
            returnStatements.add(n.getExpression().get().toString());
            super.visit(n, returnStatements);
        }
    }
    
    private static class IfVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(IfStmt n, Void arg) {
            //Check what value is being returned
        	
        	System.out.println("If Condition: " + n.getCondition());
//        	System.out.println(n.getThenStmt()); //prints out content of if-block
        	
        	//Returns Optional.empty if there is no else statement
        	System.out.println("Else statement: ");// + n.getElseStmt());
            
        	n.accept(new ReturnVisitor(), null);
        	
            super.visit(n, arg);
        }
    }
    
    private static class MethodCallVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            // Found a method call
        	System.out.println("Method name");
            System.out.println(n.getScope() + " - " + n.getName());
            System.out.println("Arguments");
            System.out.println(n.getArguments().toString() + "\n");
            // Don't forget to call super, it may find more method calls inside the arguments of this method call, for example.
            super.visit(n, arg);
        }
    }
    
//    private static class ReturnVisitor extends VoidVisitorAdapter<List<String>> {
//        @Override
//        public void visit(ReturnStmt returnStmt, List<String> returnedVal) {
//        	// TODO: insert a println before the return stmt
//        	System.out.println(returnStmt.toString());
//        	System.out.println("Returning: " + returnStmt.getExpression().toString());
//        	returnedVal.add(returnStmt.getExpression().toString());
//        	super.visit(returnStmt, returnedVal);
//        }
//    }
    
	public static void main(String [] Args) throws FileNotFoundException{
		FileInputStream in = new FileInputStream("twofer.java");
		CompilationUnit cu = JavaParser.parse(in);
		
		//Check that the class name is "twofer"
		Optional<ClassOrInterfaceDeclaration> classX = cu.getClassByName("twofer");
		if (!classX.isPresent()){
			System.out.println("Class is not properly named");
		}
		
		//Check for import statements
		if(cu.getImports().size() > 0){
			System.out.println("Simplify code to no imports?");
		}
		
		//Check in the method header what type is being returned and if there's a parameter (name)		
		cu.accept(new MethodVisitor(), null);
		System.out.println("\n");
		
		//Check the return statement
		List<String> returns = new ArrayList<String>();
		cu.accept(new ReturnVisitor(), returns);
		if(returns.size() < 1){
			System.out.println("There's no return statement");
		}
		System.out.println("\n");
		
		//If there are multiple return statements, then you want to see if there's if-else
		//cu.accept(new IfVisitor(), null);
		//System.out.println("\n");
		
		//Checks all methods being called
		cu.accept(new MethodCallVisitor(), null);
		//If there's a variable in return statement, want to check the values of the variables
		
		
	}

}
