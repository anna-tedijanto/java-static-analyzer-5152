package exercism_parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.json.*;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class Twofer_Parser {
	
	
	public static CompilationUnit getContent(String flname) throws Exception{
        return JavaParser.parse(new FileInputStream(flname));
    }
	
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
            	if(!n.getType().toString().equals("String")){
            		System.out.println("Incorrect parameter type, should be String");
            	}
            	else if(!n.getParameter(0).toString().equals("String name")){
            		System.out.println("Incorrect parameter name, should be name");
            	}
            }
            
            super.visit(n, arg);
        }
    }
    
    private static class ReturnVisitor extends VoidVisitorAdapter<List<ReturnStmt>> {
    	
    	
        @Override
        public void visit(ReturnStmt n, List<ReturnStmt> returnStatements) {
            //Check what value is being returned
        	
        	System.out.println("Returning: " + n.getExpression().get().toString());
            returnStatements.add(n);
            
            super.visit(n, returnStatements);
        }
    }
    
    private static class IfVisitor extends VoidVisitorAdapter<Void> {
    	
        boolean containsElse = false;

        @Override
        public void visit(IfStmt ifs, Void v) {
            if (ifs.getElseStmt() != null) {
                containsElse = true;
            }
        }
    }
    
    
	public static void main(String [] Args) throws Exception{
		JSONObject analysis = new JSONObject();
		String status = "refer_to_mentor";
		ArrayList<String> comments = new ArrayList<String>();
		
		CompilationUnit cu = getContent("twofer3.java");
		
		//Check that the class name is "twofer"
		Optional<ClassOrInterfaceDeclaration> classX = cu.getClassByName("twofer");
		if (!classX.isPresent()){
			comments.add("Class is not properly named.");
			System.out.println("Added comment: Class is not properly named");
			status = "disapprove_with_comments";
		}
		
		//Check the return statement
		List<ReturnStmt> returns = new ArrayList<ReturnStmt>();
		cu.accept(new ReturnVisitor(), returns);
		if(returns.size() < 1){
			comments.add("There is not return statement");
			System.out.println("Added comment: There's no return statement");
			status = "disapprove_with_comments";
		}
		
		//Check for import statements
		if(cu.getImports().size() == 1){
			if(cu.getImport(0).getName().toString().equals("java.util.Optional")){
				OptimalOptional opt = new OptimalOptional(cu);
				if (opt.parse(returns)) status = "approve_as_optimal";
			}
			if(cu.getImport(0).getName().toString().equals("java.util.Objects")){
				OptimalObjects obj = new OptimalObjects(cu);
				if (obj.parse(returns)) status = "approve_as_optimal";
			}
		}
		else if(cu.getImports().size() > 1){
			//what to do here since there shouldn't be the need for multiple imported packages
			System.out.println("too many imports");
		}
		
		//Check in the method header for what type is being returned and if there's a parameter (name)		
		cu.accept(new MethodVisitor(), null);
		System.out.println("\n");
		
		
		//If there are multiple return statements, then you want to see if there's if-else
		IfVisitor ifFinder = new IfVisitor();
		cu.accept(ifFinder, null);
		if(ifFinder.containsElse){
			comments.add("Solved with if-else statements, there are more optimal methods");
			status = "disapprove_with_comment";
			System.out.println("Added comment: Solved with if-else statements, there are more optimal methods");
		}
		
		//Checks all methods being called
//		cu.accept(new MethodCallVisitor(), null);
		//If there's a variable in return statement, want to check the values of the variables
		
		//Writing to the json file
		analysis.put("status", status);
		analysis.put("comments", comments);
		String analysisString = analysis.toString();
		System.out.println(analysisString);
		PrintWriter out = null;
		try {
		    out = new PrintWriter(new FileWriter("./analysis.json"));
		    out.write(analysisString);
		    out.close();
		} catch (Exception ex) {
		    System.out.println("error: " + ex.toString());
		}
		
	}

}
