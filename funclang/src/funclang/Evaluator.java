package funclang;
import static funclang.AST.*;
import static funclang.Value.*;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import funclang.AST.AddExp;
import funclang.AST.BoolExp;
import funclang.AST.CallExp;
import funclang.AST.CarExp;
import funclang.AST.CdrExp;
import funclang.AST.ConsExp;
import funclang.AST.DefineDecl;
import funclang.AST.DivExp;
import funclang.AST.EqualExp;
import funclang.AST.EvalExp;
import funclang.AST.Exp;
import funclang.AST.GreaterEqualExp;
import funclang.AST.GreaterExp;
import funclang.AST.IfExp;
import funclang.AST.LambdaExp;
import funclang.AST.LessEqualExp;
import funclang.AST.LessExp;
import funclang.AST.LetExp;
import funclang.AST.ListExp;
import funclang.AST.ModExp;
import funclang.AST.MultExp;
import funclang.AST.NotEqualExp;
import funclang.AST.NullExp;
import funclang.AST.NumExp;
import funclang.AST.Program;
import funclang.AST.ReadExp;
import funclang.AST.StrExp;
import funclang.AST.SubExp;
import funclang.AST.UnitExp;
import funclang.AST.VarExp;
import funclang.AST.Visitor;
import funclang.Env.*;
import funclang.Value.BoolVal;
import funclang.Value.DynamicError;
import funclang.Value.NumVal;
import funclang.Value.PairVal;
import funclang.Value.StringVal;
import funclang.Value.UnitVal;

public class Evaluator implements Visitor<Value> {
	
	/*Printer.Formatter ts = new Printer.Formatter();*/

	Env initEnv = initialEnv(); //New for definelang
	
	Value valueOf(Program p) {
			return (Value) p.accept(this, initEnv);
	}
	
	@Override
	public Value visit(AddExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 0;
		boolean Errorchecker = false;
		for(Exp exp: operands) {
			try {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result += intermediate.v(); //Semantics of AddExp in terms of the target language.
			} catch (ClassCastException ex) {
				// Handle the case where the operand is not a NumVal
				String varName = ((VarExp) exp).name(); // Get the variable name
				System.out.println("--------------------"); // Print a separator for clarity
				System.out.flush(); // Flush the output to ensure it appears immediately
				System.out.println("Error: " + ex.getMessage()); // Print the error message
				System.out.println("Addition operation requires numeric operands.");
				System.out.flush(); // Flush the output to ensure it appears immediately
				System.out.println("Expression: " + exp.accept(this, env)); // Print the expression that caused the error
				System.out.flush(); // Flush the output to ensure it appears immediately
				System.out.println("Variable: " + varName); // Print the variable name
				System.out.flush(); // Flush the output to ensure it appears immediately
				System.out.println("--------------------"); // Print a separator for clarity
				System.out.flush(); // Flush the output to ensure it appears immediately
				Errorchecker = true;
			} catch (NullPointerException ex) {
				// Handle the case where the operand is null
				System.out.println("--------------------"); // Print a separator for clarity
				System.out.flush(); // Flush the output to ensure it appears immediately
				System.out.println("Error: " + ex.getMessage()); // Print the error message
				System.out.println("Addition operation requires numeric operands.");
				System.out.flush(); // Flush the output to ensure it appears immediately
				System.out.println("Expression: " + exp.accept(this, env)); // Print the expression that caused the error
				System.out.flush(); // Flush the output to ensure it appears immediately
				System.out.println("Variable: " + ((VarExp) exp).name()); // Print the variable name
				System.out.flush(); // Flush the output to ensure it appears immediately
				Errorchecker = true;
			} 
		}
		return new NumVal(result);
	}
	
	@Override
	public Value visit(UnitExp e, Env env) {
		return new UnitVal();
	}

	@Override
	public Value visit(NumExp e, Env env) {
		return new NumVal(e.v());
	}

	@Override
	public Value visit(StrExp e, Env env) {
		return new StringVal(e.v());
	}

	@Override
	public Value visit(BoolExp e, Env env) {
		return new BoolVal(e.v());
	}
	
	@Override
	public Value visit(AST.AndExp e, Env env) {
    // Evaluate the left operand
    Value leftValue = e.left().accept(this, env);

    // Ensure the left operand is a boolean
    if (!(leftValue instanceof Value.BoolVal)) {
        return new Value.DynamicError("Left operand of ('&&'/'and') is not a boolean");
    }

    // Short-circuit: if the left operand is false, return false
    if (!((Value.BoolVal) leftValue).v()) {
        return new Value.BoolVal(false);
    }

    // Otherwise, evaluate the right operand
    Value rightValue = e.right().accept(this, env);

    // Ensure the right operand is a boolean
    if (!(rightValue instanceof Value.BoolVal)) {
        return new Value.DynamicError("Right operand of ('&&'/'and') is not a boolean");
    }

    return rightValue;
	}

	
	@Override
	public Value visit(AST.OrExp e, Env env) {
    // Evaluate the left operand
    Value leftValue = e.left().accept(this, env);

    // Ensure the left operand is a boolean
    if (!(leftValue instanceof Value.BoolVal)) {
        return new Value.DynamicError("Left operand of '||' is not a boolean");
    }

    // Short-circuit: if the left operand is true, return true
    if (((Value.BoolVal) leftValue).v()) {
        return new Value.BoolVal(true);
    }

    // Otherwise, evaluate the right operand
    Value rightValue = e.right().accept(this, env);

    // Ensure the right operand is a boolean
    if (!(rightValue instanceof Value.BoolVal)) {
        return new Value.DynamicError("Right operand of '||' is not a boolean");
    }

    return rightValue;
	}

	@Override
	public Value visit(DivExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v(); 
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			if(rVal.v() == 0) {
				return new DynamicError("Division by zero in expression " /*+ ts.visit(e, env)*/);
			}
			result = result / rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(ModExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v(); 
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result % rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(MultExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 1;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result *= intermediate.v(); //Semantics of MultExp.
		}
		return new NumVal(result);
	}
@Override
public Value visit(Program p, Env env) {
    try {
        // Process all declarations
        for (DefineDecl d : p.decls()) {
            d.accept(this, initEnv);
        }

        // Evaluate each expression in the program
        Value result = new UnitVal(); // Default to UnitVal
        for (Exp d : p.e()) { // Assuming `expressions()` returns a list of expressions
            result = d.accept(this, initEnv);
        }
        return result; // Return the result of the last expression
    } catch (ClassCastException e) {
        return new DynamicError(e.getMessage());
    }
}

	@Override
	public Value visit(SubExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v();
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result - rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(VarExp e, Env env) {
    // Look up the variable in the environment
    Value value = env.get(e.name());

    // If the variable is not found, return a dynamic error
    if (value == null) {
		System.out.println("Undefined variable: " + e.name());
		System.out.flush(); // Flush the output to ensure it appears immediately
        return new Value.DynamicError("Undefined variable: " + e.name());
    }

    // Return the value of the variable
    return value;
}	

	@Override
	public Value visit(LetExp e, Env env) { // New for varlang.
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();
		List<Value> values = new ArrayList<Value>(value_exps.size());
		
		for(Exp exp : value_exps) 
			values.add((Value)exp.accept(this, env));
		
		Env new_env = env;
		for (int index = 0; index < names.size(); index++)
			new_env = new ExtendEnv(new_env, names.get(index), values.get(index));

		return (Value) e.body().accept(this, new_env);		
	}	


	
    @Override
    public Value visit(AST.PrintExp e, Env env) {
        List<Exp> exps = e.exps();
        StringBuilder output = new StringBuilder();

        for (Exp exp : exps) {
            Value value = exp.accept(this, env);
            if (value instanceof Value.StringVal) {
                output.append(((Value.StringVal) value).v().replace("\"", ""));
            } else if (value instanceof Value.NumVal) {
                double num = ((Value.NumVal) value).v();
                // Check if the number is an integer by casting to int
				// and comparing with the original number
                if (num == (int) num) {
                    output.append((int) num); // Print as an integer
                } else {
                    output.append(num); // Print as a double
                }
            } else if (value instanceof Value.BoolVal) {
                output.append(((Value.BoolVal) value).v());
            } else if (value instanceof Value.PairVal) {
                output.append(((Value.PairVal) value).tostring());
            } else if (value instanceof Value.UnitVal) {
                output.append("Unit");
            } else {
				output.append("Printing error: ");
                output.append("Unknown value type");
            }
            output.append(" "); // Add a space between items
        }

        System.out.println(output.toString().trim()); // Print the concatenated output
		System.out.flush(); // Flush the output to ensure it appears immediately
        return new Value.UnitVal(); // Return UnitVal
    }

	@Override
	public Value visit(DefineDecl e, Env env) { // New for definelang.
		String name = e.name();
		Exp value_exp = e.value_exp();
		Value value = (Value) value_exp.accept(this, env);
		((GlobalEnv) initEnv).extend(name, value);
		return value;		
	}
	

	@Override
	public Value visit(LambdaExp e, Env env) {
        // Create a function value with three components:
		//  1. formal parameters of the function - e.formals()
		//  2. actual body of the function - e.body()
		//  3. mapping from the free variables in the function body to their values.
		return new Value.FunVal(env, e.formals(), e.body());
	}
	
	@Override
	public Value visit(CallExp e, Env env) { // New for funclang.
		Object result = e.operator().accept(this, env);
		if(!(result instanceof Value.FunVal))
			return new Value.DynamicError("Operator not a function in call " /*+  ts.visit(e, env)*/);
		Value.FunVal operator =  (Value.FunVal) result; //Dynamic checking
		List<Exp> operands = e.operands();

		// Call-by-value semantics
		List<Value> actuals = new ArrayList<Value>(operands.size());
		for(Exp exp : operands) 
			actuals.add((Value)exp.accept(this, env));
		
		List<String> formals = operator.formals();
 		if (formals.size()!=actuals.size())
			return new Value.DynamicError("Argument mismatch in call " /* + ts.visit(e, env) */);

		Env fun_env = operator.env();
		for (int index = 0; index < formals.size(); index++)
			fun_env = new ExtendEnv(fun_env, formals.get(index), actuals.get(index));
		
		return (Value) operator.body().accept(this, fun_env);
	}
		


	@Override
	public Value visit(LessExp e, Env env) {
    Value first = e.first_exp().accept(this, env);
    Value second = e.second_exp().accept(this, env);



    // Check if both operands are numbers
    if (first instanceof Value.NumVal && second instanceof Value.NumVal) {
        return new Value.BoolVal(((Value.NumVal) first).v() < ((Value.NumVal) second).v());
    }
	// Check if both operands are strings
	if (first instanceof Value.StringVal && second instanceof Value.StringVal) {
		return new Value.BoolVal(((Value.StringVal) first).v().compareTo(((Value.StringVal) second).v()) < 0);
	}

	// Error Messages
	String firstType = first.getClass().getSimpleName();
	String secondType = second.getClass().getSimpleName();
	// VarExp firstVar = (VarExp) e.first_exp();
	// VarExp secondVar = (VarExp) e.second_exp();

    // Handle type mismatch by printing error messages
    System.out.println("--------------------");
    System.out.println("Error: Comparison operation ('<') requires numeric operands.");
    System.out.println("Expression causing the issue: " + 
		(e.first_exp() instanceof VarExp ? ((VarExp) e.first_exp()).name() : "unknown") + 
		" < " + 
		(e.second_exp() instanceof VarExp ? ((VarExp) e.second_exp()).name() : "unknown"));
    System.out.println("First operand value: " + first.tostring() + " (type: " + firstType + ")");
    System.out.println("Second operand value: " + second.tostring() + " (type: " + secondType + ")");
    System.out.println("--------------------");

    // Return a dynamic error with a detailed message
    return new Value.DynamicError(
        "Comparison operation ('<') requires numeric operands. " +
        "First operand type: " + first.getClass().getSimpleName() + ", " +
        "Second operand type: " + second.getClass().getSimpleName()
    );
}

	@Override
	public Value visit(EqualExp e, Env env) {
    // Evaluate the first and second expressions
    Value firstValue = e.first_exp().accept(this, env);
    Value secondValue = e.second_exp().accept(this, env);

    // Check if both operands are numbers
    if (firstValue instanceof Value.NumVal && secondValue instanceof Value.NumVal) {
        return new Value.BoolVal(((Value.NumVal) firstValue).v() == ((Value.NumVal) secondValue).v());
    }

    // Check if both operands are booleans
    if (firstValue instanceof Value.BoolVal && secondValue instanceof Value.BoolVal) {
        return new Value.BoolVal(((Value.BoolVal) firstValue).v() == ((Value.BoolVal) secondValue).v());
    }

	// Check if both operands are strings
	if(firstValue instanceof Value.StringVal && secondValue instanceof Value.StringVal) {
		return new Value.BoolVal(((Value.StringVal) firstValue).v().equals(((Value.StringVal) secondValue).v()));
	}

    // Handle type mismatch
    String firstType = firstValue.getClass().getSimpleName();
    String secondType = secondValue.getClass().getSimpleName();

    System.out.println("--------------------");
    System.out.println("Error: Equality operation ('==') requires numeric or boolean operands.");
    System.out.println("Expression causing the issue: " + 
        (e.first_exp() instanceof VarExp ? ((VarExp) e.first_exp()).name() : "unknown") + 
        " == " + 
        (e.second_exp() instanceof VarExp ? ((VarExp) e.second_exp()).name() : "unknown"));
    System.out.println("First operand value: " + firstValue.tostring() + " (type: " + firstType + ")");
    System.out.println("Second operand value: " + secondValue.tostring() + " (type: " + secondType + ")");
    System.out.println("--------------------");

    // Return a dynamic error with a detailed message
    return new Value.DynamicError(
        "Equality operation ('==') requires numeric or boolean operands. " +
        "First operand type: " + firstType + ", " +
        "Second operand type: " + secondType
    );
}

	@Override
	public Value visit(NotEqualExp e, Env env) { // New for funclang.
		Value first = e.first_exp().accept(this, env);
		Value second = e.second_exp().accept(this, env);

		// Check if both operands are numbers
		if (first instanceof Value.NumVal && second instanceof Value.NumVal) {
			return new Value.BoolVal(((Value.NumVal) first).v() != ((Value.NumVal) second).v());
		}

		// Check if both operands are booleans
		if (first instanceof Value.BoolVal && second instanceof Value.BoolVal) {
			return new Value.BoolVal(((Value.BoolVal) first).v() != ((Value.BoolVal) second).v());
		}

		// Check if both operands are strings
		if (first instanceof Value.StringVal && second instanceof Value.StringVal) {
			return new Value.BoolVal(!((Value.StringVal) first).v().equals(((Value.StringVal) second).v()));
		}

		// Handle type mismatch
		String firstType = first.getClass().getSimpleName();
		String secondType = second.getClass().getSimpleName();

		System.out.println("--------------------");
		System.out.println("Error: Inequality operation ('!=') requires numeric, boolean, or string operands.");
		System.out.println("Expression causing the issue: " + 
			(e.first_exp() instanceof VarExp ? ((VarExp) e.first_exp()).name() : "unknown") + 
			" != " + 
			(e.second_exp() instanceof VarExp ? ((VarExp) e.second_exp()).name() : "unknown"));
		System.out.println("First operand value: " + first.tostring() + " (type: " + firstType + ")");
		System.out.println("Second operand value: " + second.tostring() + " (type: " + secondType + ")");
		System.out.println("--------------------");

		// Return a dynamic error with a detailed message
		return new Value.DynamicError(
			"Inequality operation ('!=') requires numeric, boolean, or string operands. " +
			"First operand type: " + firstType + ", " +
			"Second operand type: " + secondType
		);
	}

	@Override
	public Value visit(GreaterEqualExp e, Env env) { // New for funclang.
		Value.NumVal first = (Value.NumVal) e.first_exp().accept(this, env);
		Value.NumVal second = (Value.NumVal) e.second_exp().accept(this, env);
		return new Value.BoolVal(first.v() >= second.v());
	}

	
	@Override
	public Value visit(LessEqualExp e, Env env) { // New for funclang.
		Value.NumVal first = (Value.NumVal) e.first_exp().accept(this, env);
		Value.NumVal second = (Value.NumVal) e.second_exp().accept(this, env);
		return new Value.BoolVal(first.v() <= second.v());
	}


	@Override
	public Value visit(GreaterExp e, Env env) { // New for funclang.
		Value.NumVal first = (Value.NumVal) e.first_exp().accept(this, env);
		Value.NumVal second = (Value.NumVal) e.second_exp().accept(this, env);
		return new Value.BoolVal(first.v() > second.v());
	}
	
	@Override
	public Value visit(CarExp e, Env env) { 
		Value.PairVal pair = (Value.PairVal) e.arg().accept(this, env);
		return pair.fst();
	}
	
	@Override
	public Value visit(CdrExp e, Env env) { 
		Value.PairVal pair = (Value.PairVal) e.arg().accept(this, env);
		return pair.snd();
	}
	
	@Override
	public Value visit(ConsExp e, Env env) { 
		Value first = (Value) e.fst().accept(this, env);
		Value second = (Value) e.snd().accept(this, env);
		return new Value.PairVal(first, second);
	}


	@Override
	public Value visit(ListExp e, Env env) { // New for funclang.
		List<Exp> elemExps = e.elems();
		int length = elemExps.size();
		if(length == 0)
			return new Value.Null();
		
		//Order of evaluation: left to right e.g. (list (+ 3 4) (+ 5 4)) 
		Value[] elems = new Value[length];
		for(int i=0; i<length; i++)
			elems[i] = (Value) elemExps.get(i).accept(this, env);
		
		Value result = new Value.Null();
		for(int i=length-1; i>=0; i--) 
			result = new PairVal(elems[i], result);
		return result;
	}	
	
	@Override
	public Value visit(NullExp e, Env env) {
		Value val = (Value) e.arg().accept(this, env);
		return new BoolVal(val instanceof Value.Null);
	}
	

	public Value visit(EvalExp e, Env env) {
		StringVal programText = (StringVal) e.code().accept(this, env);
		Program p = _reader.parse(programText.v());
		return (Value) p.accept(this, env);
	}

	public Value visit(ReadExp e, Env env) {
		StringVal fileName = (StringVal) e.file().accept(this, env);
		try {
			String text = Reader.readFile("" + System.getProperty("user.dir") + File.separator + fileName.v());
			return new StringVal(text);
		} catch (IOException ex) {
			return new DynamicError(ex.getMessage());
		}
	}

	
	public Value visit(AST.WhileExp e, Env env) {
	    while (true) {
	        Value conditionValue = e.condition().accept(this, env);
	        if (!(conditionValue instanceof Value.BoolVal)) {
	            return new Value.DynamicError("Condition must evaluate to a boolean for While Loop.");
	        }
	        if (!((Value.BoolVal) conditionValue).v()) {
	            break;
	        }
	        for (Exp stmt : e.body()) { // Iterate over the list of expressions
	            stmt.accept(this, env);
	        }
	    }
	    return new Value.UnitVal(); // Return a unit value after the loop ends
	}

	@Override
	public Value visit(IfExp e, Env env) { // New for funclang.
    Object result = e.conditional().accept(this, env);
    if (!(result instanceof Value.BoolVal)) {
        return new Value.DynamicError("Condition not a boolean in expression");
    }
    Value.BoolVal condition = (Value.BoolVal) result; // Dynamic checking

    List<Exp> bodyToExecute = condition.v() ? e.then_exp() : e.else_exp(); // Choose the correct body

    Value lastValue = new Value.UnitVal(); // Default to UnitVal
    for (Exp stmt : bodyToExecute) { // Iterate over the list of expressions
        lastValue = stmt.accept(this, env); // Evaluate each expression
    }
    return lastValue; // Return the result of the last expression
}



@Override
public Value visit(AST.IncExp e, Env env) {
    // Ensure the expression is a variable
    if (!(e.fst() instanceof AST.VarExp)) {
        return new Value.DynamicError("Increment operation requires a variable.");
    }

    // Get the variable name
    String varName = ((AST.VarExp) e.fst()).name();

    // Retrieve the current value of the variable
    Value value = env.get(varName);

    // Ensure the value is numeric
    if (value instanceof NumVal) {
        double incrementedValue = ((NumVal) value).v() + 1; // Increment the numeric value

        // Update the variable in the environment
        ((GlobalEnv) env).extend(varName, new NumVal(incrementedValue));

        // Return the incremented value
        return new NumVal(incrementedValue);
    }

    // Return an error if the value is not numeric
    return new Value.DynamicError("Increment operation is only valid for numbers.");
}

@Override
public Value visit(AST.DeIncExp e, Env env) {
    // Ensure the expression is a variable
    if (!(e.fst() instanceof AST.VarExp)) {
        return new Value.DynamicError("Increment operation requires a variable.");
    }

    // Get the variable name
    String varName = ((AST.VarExp) e.fst()).name();

    // Retrieve the current value of the variable
    Value value = env.get(varName);

    // Ensure the value is numeric
    if (value instanceof NumVal) {
        double incrementedValue = ((NumVal) value).v() - 1; // Increment the numeric value

        // Update the variable in the environment
        ((GlobalEnv) env).extend(varName, new NumVal(incrementedValue));

        // Return the incremented value
        return new NumVal(incrementedValue);
    }

    // Return an error if the value is not numeric
    return new Value.DynamicError("Increment operation is only valid for numbers.");
}

@Override
public Value visit(AST.InputExp e, Env env) {
    // Evaluate the prompt expression
    Value promptValue = e.prompt().accept(this, env);

    // Ensure the prompt is a string
    if (!(promptValue instanceof Value.StringVal)) {
        return new Value.DynamicError("Input prompt must be a string");
    }

    // Display the prompt (if not empty)
    String prompt = ((Value.StringVal) promptValue).v();
    if (!prompt.isEmpty()) {
        System.out.println(prompt.replace("\"", "")); // Remove quotes
        System.out.flush(); // Flush the output to ensure the prompt appears immediately
    }
	else {
		System.out.println("Please enter a value: ");
		System.out.flush(); // Flush the output to ensure the prompt appears immediately
	}
    // Read user input
    Scanner scanner = new Scanner(System.in);
    String userInput = scanner.nextLine();
    // Return the user input as a string)
	if (userInput.matches(".*[a-zA-Z].*")) {
        System.out.println("The userInput contains characters.");
		System.out.flush(); // Flush the output to ensure it appears immediately
		return new Value.StringVal(userInput);
	}
	else if (userInput.matches(".*[0-9].*")) {
		System.out.println("The userInput contains numbers.");
		System.out.flush(); // Flush the output to ensure it appears immediately
		return new Value.NumVal(Double.parseDouble(userInput));
	}
	else if (userInput.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
		System.out.println("The userInput contains special characters.");
		System.out.flush(); // Flush the output to ensure it appears immediately
		return new Value.StringVal(userInput);
	}
	else {
		System.out.println("The userInput is empty or contains no valid characters.");
		System.out.flush(); // Flush the output to ensure it appears immediately
		return new Value.StringVal(userInput);
	}
    
}

	private Env initialEnv() {
		GlobalEnv initEnv = new GlobalEnv();
		
		/* Procedure: (read <filename>). Following is same as (define read (lambda (file) (read file))) */
		List<String> formals = new ArrayList<>();
		formals.add("file");
		Exp body = new AST.ReadExp(new VarExp("file"));
		Value.FunVal readFun = new Value.FunVal(initEnv, formals, body);
		initEnv.extend("read", readFun);

		/* Procedure: (require <filename>). Following is same as (define require (lambda (file) (eval (read file)))) */
		formals = new ArrayList<>();
		formals.add("file");
		body = new EvalExp(new AST.ReadExp(new VarExp("file")));
		Value.FunVal requireFun = new Value.FunVal(initEnv, formals, body);
		initEnv.extend("require", requireFun);
		
		/* Add new built-in procedures here */ 
		
		return initEnv;
	}
	
	Reader _reader; 
	public Evaluator(Reader reader) {
		_reader = reader;
	}
}
