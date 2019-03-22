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
    	
    	private boolean optionalUse;
    	
    	public  ReturnVisitor(boolean optionalUse){
    		this.optionalUse = optionalUse;
    	}
    	
        @Override
        public void visit(ReturnStmt n, List<ReturnStmt> returnStatements) {
            //Check what value is being returned
        	
        	System.out.println("Returning: " + n.getExpression().get().toString());
            returnStatements.add(n);
            
            if(this.optionalUse){
            	n.accept(new OptionalMethodVisitor(), null);
            }
            
            
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
        	//System.out.println("If Condition: " + ifs.getCondition());
        	//System.out.println(n.getThenStmt()); //prints out content of if-block
        	
        	//Returns Optional.empty if there is no else statement
        	//System.out.println("Else statement: ");// + n.getElseStmt());
            
            //super.visit(n, arg);
        }
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
    
    private static boolean objectsUse(List<ReturnStmt> returns){
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
    
    private static boolean optionalUse(List<ReturnStmt> returns){
    	if(returns.size() > 1){
    		//when would this apply?
    		System.out.println("Can be refactored to use less return statements");    		
        	return false;
    	}
    	else{
    		ReturnStmt n = returns.get(0);
    		List<MethodCallExpr> returnMethods = new ArrayList<MethodCallExpr>();
    		n.accept(new OptionalMethodVisitor(), returnMethods);
    		if(returnMethods.size() == 3){
    			if(returnMethods.get(0).getScope().get().toString().equals("String") &&
    					returnMethods.get(0).getNameAsString().equals("format")){
    				if(!returnMethods.get(0).getArgument(0).toString().equals("\"One for %s, one for me.\"")){
//    					System.out.println(returnMethods.get(0).getArgument(0).toStringLiteralExpr().get().asString());
    					return false;
    				};
    			}
    			if(returnMethods.get(1).getScope().get().toString().equals("Optional.ofNullable(name)") &&
    					returnMethods.get(1).getNameAsString().equals("orElse")){
    				if(!returnMethods.get(1).getArgument(0).toString().equals("\"you\"")){
    					return false;
    				};
    			}
    			if(returnMethods.get(2).getScope().get().toString().equals("Optional") &&
    					returnMethods.get(2).getNameAsString().equals("orNullable")){
    				if(!returnMethods.get(2).getArgument(0).toString().equals("name")){
    					return false;
    				};
    			}
    		}
    		return true;
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
		cu.accept(new ReturnVisitor(false), returns);
		if(returns.size() < 1){
			comments.add("There is not return statement");
			System.out.println("Added comment: There's no return statement");
			status = "disapprove_with_comments";
		}
		
		//Check for import statements
		if(cu.getImports().size() == 1){
			if(cu.getImport(0).getName().toString().equals("java.util.Optional")){
				if (optionalUse(returns)) status = "approve_as_optimal";
			}
			if(cu.getImport(0).getName().toString().equals("java.util.Objects")){
				if (objectsUse(returns)) status = "approve_as_optimal";
			}
		}
		else if(cu.getImports().size() > 1){
			//what to do here?
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
