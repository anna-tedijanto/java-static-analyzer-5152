package exercism_parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.*;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
//import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.observer.AstObserver;
import com.github.javaparser.ast.observer.AstObserverAdapter;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.JsonPrinter;

public class Parser {
    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            /* here you can access the attributes of the method.
             this method will be called for all methods in this 
             CompilationUnit, including inner class methods */
//            System.out.println(n.getName());
            super.visit(n, arg);
        }
    }
    
    private static class ReturnVisitor extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(ReturnStmt returnStmt, List<String> returnedVal) {
        	// TODO: insert a println before the return stmt
        	System.out.println(returnStmt.toString());
        	System.out.println("Returning: " + returnStmt.getExpression().toString());
        	returnedVal.add(returnStmt.getExpression().toString());
        	super.visit(returnStmt, returnedVal);
        }
    }
    
    //From Peter's code
    private static class HelloWorldFinder extends VoidVisitorAdapter<Void> {
        boolean found = false;

        @Override
        public void visit(ReturnStmt ret, Void v) {
        	if(ret.getExpression().get().isTypeExpr()){
        		StringLiteralExpr sle = ret.getExpression().get().asStringLiteralExpr();
                found = found || sle.asString().equals("Hello, World!");
        	}
        }
      }
    
    
    private static class VariableVisitor extends VoidVisitorAdapter<HashMap> {
        @Override
        public void visit(VariableDeclarator variable, HashMap collector) {
        	// TODO: insert a println before the return stmt
//        	System.out.println(variable.toString());
        	super.visit(variable, collector);
        	System.out.println(variable.getNameAsString());
        	System.out.println(variable.getInitializer().get()); 
        	//gets the initial value of variable, but is wrapped in Optional[value]
        	collector.put(variable.getNameAsString(), variable.getInitializer());
//        	return returnStmt;
        }
    }
    
    
    public static void main(String [] Args){
//    	Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
    	
    	String code = "class HelloWorld { String helloWorld() {String x = \"Hello World!\"; return x;}}";
//    	String code = "class A { int f = 2; String g = '5'; void foo(int p) { return 'z'; }}";
    	
//    	FileInputStream in = new FileInputStream("test.java");
    	CompilationUnit cu = JavaParser.parse(code);
    	JsonPrinter printer = new JsonPrinter(true);
    	System.out.println(printer.output(cu));
    	
    	//Testing out method that is supposed to print all method names
    	cu.accept(new MethodVisitor(), null);
    	
    	//Collect what is being returned
    	List<String> returnedValue = new ArrayList<String>();
    	new ReturnVisitor().visit(cu, returnedValue);
    	
    	//Collecting variable name, values
    	HashMap<String, Optional> variableNames = new HashMap();
    	VoidVisitor<HashMap> variableNameCollector = new VariableVisitor();
    	variableNameCollector.visit(cu, variableNames);
    	variableNames.keySet().forEach(n -> System.out.println("Variable Collected: " + n));
    	
    	//Just check if the string "Hello World!" is returned
    	HelloWorldFinder hwf = new HelloWorldFinder();
        cu.accept(hwf, null);
        
    	if(hwf.found){
    		System.out.println("Code works!");
    	}
    	//Use regex to get the variable name between brackets? Right now it appears as Optional[Variable_Name]
    	else{
    		String returnedVariable = returnedValue.get(0);
    		String answer = returnedVariable.substring(returnedVariable.indexOf("[")+1, returnedVariable.indexOf("]"));
    		if(variableNames.containsKey(answer)){
    			String variableValue = variableNames.get(answer).get().toString();
    			if(variableValue.equals("\"Hello World!\"")){
    				System.out.println("Code works!");
    			}
    			
    		}
    		else{
    			System.out.println("Code doesn't work");
    		}
    		
    		
    	}
    	

    }
}


