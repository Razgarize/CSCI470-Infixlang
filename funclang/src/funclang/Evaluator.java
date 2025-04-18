package funclang;
import static funclang.AST.*;
import static funclang.Value.*;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.Random;

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

    for (Exp exp : operands) {
        Value value = exp.accept(this, env);

        // Ensure the operand is numeric
        if (!(value instanceof NumVal)) {
            System.out.println("--------------------");
            System.out.println("Error: Addition operation requires numeric operands.");
            System.out.println("Expression: " + exp.getClass().getSimpleName());
            if (exp instanceof VarExp) {
                System.out.println("Variable: " + ((VarExp) exp).name());
            } else if (value instanceof StringVal) {
                System.out.println("String: " + ((StringVal) value).v());
            } else if (value instanceof BoolVal) {
                System.out.println("Boolean: " + ((BoolVal) value).v());
            } else if (value instanceof UnitVal) {
                System.out.println("Unit: Unit");
            } else if (value instanceof PairVal) {
                System.out.println("Pair: " + ((PairVal) value).toString());
            } else {
                System.out.println("Unknown type: " + value.getClass().getSimpleName());
            }
			System.out.println("Value: " + value.tostring() + " (type: " + value.getClass().getSimpleName() + ")");
            System.out.println("Hint: Ensure all operands are numbers.");
            System.out.println("--------------------");
            return new DynamicError("Addition operation requires numeric operands.");
        }

        // Add the numeric value to the result
        result += ((NumVal) value).v();
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
		System.out.println("--------------------");
		System.out.println("Error: Left operand of ('&&'/'and') is not a boolean.");
		System.out.println("Expression: " + e.left().getClass().getSimpleName());
		if (e.left() instanceof VarExp) {
			System.out.println("Variable: " + ((VarExp) e.left()).name());
		} else if (leftValue instanceof StringVal) {
			System.out.println("String: " + ((StringVal) leftValue).v());
		} else if (leftValue instanceof BoolVal) {
			System.out.println("Boolean: " + ((BoolVal) leftValue).v());
		} else if (leftValue instanceof UnitVal) {
			System.out.println("Unit: Unit");
		} else if (leftValue instanceof PairVal) {
			System.out.println("Pair: " + ((PairVal) leftValue).tostring());
		} else {
			System.out.println("Unknown type: " + leftValue.getClass().getSimpleName());
		}
		System.out.println("Value: " + leftValue.tostring() + " (type: " + leftValue.getClass().getSimpleName() + ")");
		System.out.println("Hint: Ensure all operands are booleans.");
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
		System.out.println("--------------------");
		System.out.println("Error: Right operand of ('&&'/'and') is not a boolean.");
		System.out.println("Expression: " + e.right().getClass().getSimpleName());
		if (e.right() instanceof VarExp) {
			System.out.println("Variable: " + ((VarExp) e.right()).name());
		} else if (rightValue instanceof StringVal) {
			System.out.println("String: " + ((StringVal) rightValue).v());
		} else if (rightValue instanceof BoolVal) {
			System.out.println("Boolean: " + ((BoolVal) rightValue).v());
		} else if (rightValue instanceof UnitVal) {
			System.out.println("Unit: Unit");
		} else if (rightValue instanceof PairVal) {
			System.out.println("Pair: " + ((PairVal) rightValue).tostring());
		} else {
			System.out.println("Unknown type: " + rightValue.getClass().getSimpleName());
		}
		System.out.println("Value: " + rightValue.tostring() + " (type: " + rightValue.getClass().getSimpleName() + ")");
		System.out.println("Hint: Ensure all operands are booleans.");
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
		System.out.println("--------------------");
		System.out.println("Error: Left operand of '(or/||)' is not a boolean.");
		System.out.println("Expression: " + e.left().getClass().getSimpleName());
		if (e.left() instanceof VarExp) {
			System.out.println("Variable: " + ((VarExp) e.left()).name());
		} else if (leftValue instanceof StringVal) {
			System.out.println("String: " + ((StringVal) leftValue).v());
		} else if (leftValue instanceof BoolVal) {
			System.out.println("Boolean: " + ((BoolVal) leftValue).v());
		} else if (leftValue instanceof UnitVal) {
			System.out.println("Unit: Unit");
		} else if (leftValue instanceof PairVal) {
			System.out.println("Pair: " + ((PairVal) leftValue).tostring());
		} else {
			System.out.println("Unknown type: " + leftValue.getClass().getSimpleName());
		}
		System.out.println("Value: " + leftValue.tostring() + " (type: " + leftValue.getClass().getSimpleName() + ")");
		System.out.println("Hint: Ensure all operands are booleans.");
        return new Value.DynamicError("Left operand of '(or/||)' is not a boolean");
    }

    // Short-circuit: if the left operand is true, return true
    if (((Value.BoolVal) leftValue).v()) {
        return new Value.BoolVal(true);
    }

    // Otherwise, evaluate the right operand
    Value rightValue = e.right().accept(this, env);

    // Ensure the right operand is a boolean
    if (!(rightValue instanceof Value.BoolVal)) {
		System.out.println("--------------------");
		System.out.println("Error: Right operand of '(or/||)' is not a boolean.");
		System.out.println("Expression: " + e.right().getClass().getSimpleName());
		if (e.right() instanceof VarExp) {
			System.out.println("Variable: " + ((VarExp) e.right()).name());
		} else if (rightValue instanceof StringVal) {
			System.out.println("String: " + ((StringVal) rightValue).v());
		} else if (rightValue instanceof BoolVal) {
			System.out.println("Boolean: " + ((BoolVal) rightValue).v());
		} else if (rightValue instanceof UnitVal) {
			System.out.println("Unit: Unit");
		} else if (rightValue instanceof PairVal) {
			System.out.println("Pair: " + ((PairVal) rightValue).tostring());
		} else {
			System.out.println("Unknown type: " + rightValue.getClass().getSimpleName());
		}
		System.out.println("Value: " + rightValue.tostring() + " (type: " + rightValue.getClass().getSimpleName() + ")");
		System.out.println("Hint: Ensure all operands are booleans.");
        return new Value.DynamicError("Right operand of '(or/||)' is not a boolean");
    }

    return rightValue;
	}

	@Override
	public Value visit(DivExp e, Env env) {
    List<Exp> operands = e.all();
    Value firstOperand = operands.get(0).accept(this, env);

    // Ensure the first operand is numeric
    if (!(firstOperand instanceof NumVal)) {
        System.out.println("--------------------");
        System.out.println("Error: Division operation requires numeric operands.");
        System.out.println("First operand is not numeric.");
        System.out.println("Value: " + firstOperand.tostring());
		if(operands.get(0) instanceof VarExp)
			System.out.println("Variable: " + ((VarExp) operands.get(0)).name());
		else
			System.out.println("Expression: " + operands.get(0).accept(this, env).tostring() + " (" + operands.get(0).getClass().getSimpleName() + ')');
        System.out.println("Type: " + firstOperand.getClass().getSimpleName());
        System.out.println("Hint: Ensure all operands are numbers.");
        System.out.println("--------------------");
        return new DynamicError("Division operation requires numeric operands.");
    }

    double result = ((NumVal) firstOperand).v();

    for (int i = 1; i < operands.size(); i++) {
        Value nextOperand = operands.get(i).accept(this, env);

        // Ensure the next operand is numeric
        if (!(nextOperand instanceof NumVal)) {
            System.out.println("--------------------");
            System.out.println("Error: Division operation requires numeric operands.");
            System.out.println("Operand is not numeric: " + nextOperand.tostring() + " (type: " + nextOperand.getClass().getSimpleName() + ")");
			if(operands.get(i) instanceof VarExp)
			System.out.println("Variable: " + ((VarExp) operands.get(i)).name());
		else
			System.out.println("Expression: " + operands.get(i).accept(this, env).tostring() + " (" + operands.get(i).getClass().getSimpleName() + ')');
			System.out.println("Type: " + nextOperand.getClass().getSimpleName());
			System.out.println("Hint: Ensure all operands are numbers.");
            System.out.println("--------------------");
			System.out.flush(); // Flush the output to ensure it appears immediately
            return new DynamicError("Division operation requires numeric operands.");
        }

        double nextValue = ((NumVal) nextOperand).v();

        // Check for division by zero
        if (nextValue == 0) {
            System.out.println("--------------------");
            System.out.println("Error: Division by zero detected.");
            System.out.println("Expression causing the issue: " + 
                (operands.get(i) instanceof VarExp ? ((VarExp) operands.get(i)).name() : "unknown"));
            System.out.println("Left operand value: " + result + " (type: NumVal)");
            System.out.println("Right operand value: " + nextValue + " (type: NumVal)");
            System.out.println("--------------------");
            return new DynamicError("Division by zero in expression.");
        }

        // Perform the division
        result = result / nextValue;
    }

    return new NumVal(result);
}

	@Override
	public Value visit(ModExp e, Env env) {
		List<Exp> operands = e.all();
		Value firstOperand = operands.get(0).accept(this, env);

		// Ensure the first operand is numeric
		if (!(firstOperand instanceof NumVal)) {
			System.out.println("--------------------");
			System.out.println("Error: Modulus operation requires numeric operands.");
			System.out.println("First operand is not numeric.");
			System.out.println("Value: " + firstOperand.tostring());
			if (operands.get(0) instanceof VarExp)
				System.out.println("Variable: " + ((VarExp) operands.get(0)).name());
			else
				System.out.println("Expression: " + operands.get(0).accept(this, env).tostring() + " (" + operands.get(0).getClass().getSimpleName() + ')');
			System.out.println("Type: " + firstOperand.getClass().getSimpleName());
			System.out.println("Hint: Ensure all operands are numbers.");
			System.out.println("--------------------");
			return new DynamicError("Modulus operation requires numeric operands.");
		}

		double result = ((NumVal) firstOperand).v();

		for (int i = 1; i < operands.size(); i++) {
			Value nextOperand = operands.get(i).accept(this, env);

			// Ensure the next operand is numeric
			if (!(nextOperand instanceof NumVal)) {
				System.out.println("--------------------");
				System.out.println("Error: Modulus operation requires numeric operands.");
				System.out.println("Operand is not numeric: " + nextOperand.tostring() + " (type: " + nextOperand.getClass().getSimpleName() + ")");
				if (operands.get(i) instanceof VarExp)
					System.out.println("Variable: " + ((VarExp) operands.get(i)).name());
				else
					System.out.println("Expression: " + operands.get(i).accept(this, env).tostring() + " (" + operands.get(i).getClass().getSimpleName() + ')');
				System.out.println("Type: " + nextOperand.getClass().getSimpleName());
				System.out.println("Hint: Ensure all operands are numbers.");
				System.out.println("--------------------");
				System.out.flush(); // Flush the output to ensure it appears immediately
				return new DynamicError("Modulus operation requires numeric operands.");
			}

			double nextValue = ((NumVal) nextOperand).v();

			// Check for modulus by zero
			if (nextValue == 0) {
				System.out.println("--------------------");
				System.out.println("Error: Modulus by zero detected.");
				System.out.println("Expression causing the issue: " +
					(operands.get(i) instanceof VarExp ? ((VarExp) operands.get(i)).name() : "unknown"));
				System.out.println("Left operand value: " + result + " (type: NumVal)");
				System.out.println("Right operand value: " + nextValue + " (type: NumVal)");
				System.out.println("--------------------");
				return new DynamicError("Modulus by zero in expression.");
			}

			// Perform the modulus operation
			result = result % nextValue;
		}

		return new NumVal(result);
	}

	@Override
	public Value visit(MultExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 1;

		for (Exp exp : operands) {
			Value value = exp.accept(this, env);

			// Ensure the operand is numeric
			if (!(value instanceof NumVal)) {
				System.out.println("--------------------");
				System.out.println("Error: Multiplication operation requires numeric operands.");
				System.out.println("Expression: " + exp.getClass().getSimpleName());
				if (exp instanceof VarExp) {
					System.out.println("Variable: " + ((VarExp) exp).name());
				} else if (value instanceof StringVal) {
					System.out.println("String: " + ((StringVal) value).v());
				} else if (value instanceof BoolVal) {
					System.out.println("Boolean: " + ((BoolVal) value).v());
				} else if (value instanceof UnitVal) {
					System.out.println("Unit: Unit");
				} else if (value instanceof PairVal) {
					System.out.println("Pair: " + ((PairVal) value).toString());
				} else {
					System.out.println("Unknown type: " + value.getClass().getSimpleName());
				}
				System.out.println("Value: " + value.tostring() + " (type: " + value.getClass().getSimpleName() + ")");
				System.out.println("Hint: Ensure all operands are numbers.");
				System.out.println("--------------------");
				return new DynamicError("Multiplication operation requires numeric operands.");
			}

			// Multiply the numeric value to the result
			result *= ((NumVal) value).v();
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

    // Ensure there is at least one operand
    if (operands.isEmpty()) {
        System.out.println("--------------------");
        System.out.println("Error: Subtraction operation requires at least one operand.");
        System.out.println("--------------------");
        return new DynamicError("Subtraction operation requires at least one operand.");
    }

    // Evaluate the first operand
    Value firstValue = operands.get(0).accept(this, env);

    // Ensure the first operand is numeric
    if (!(firstValue instanceof NumVal)) {
        System.out.println("--------------------");
        System.out.println("Error: Subtraction operation requires numeric operands.");
        System.out.println("First operand is not numeric.");
        System.out.println("Value: " + firstValue.tostring() + " (type: " + firstValue.getClass().getSimpleName() + ")");
		if (operands.get(0) instanceof VarExp) {
			System.out.println("Variable: " + ((VarExp) operands.get(0)).name());
		} else {
			System.out.println("Expression: " + operands.get(0).accept(this, env).tostring() + " (" + operands.get(0).getClass().getSimpleName() + ')');
		}
        System.out.println("--------------------");
        return new DynamicError("Subtraction operation requires numeric operands.");
    }

    double result = ((NumVal) firstValue).v();

    // Process the remaining operands
    for (int i = 1; i < operands.size(); i++) {
        Value nextValue = operands.get(i).accept(this, env);

        // Ensure the next operand is numeric
        if (!(nextValue instanceof NumVal)) {
            System.out.println("--------------------");
            System.out.println("Error: Subtraction operation requires numeric operands.");
            System.out.println("Operand is not numeric: " + nextValue.tostring() + " (type: " + nextValue.getClass().getSimpleName() + ")");
			if (operands.get(i) instanceof VarExp) {
				System.out.println("Variable: " + ((VarExp) operands.get(i)).name());
			} else {
				System.out.println("Expression: " + operands.get(i).accept(this, env).tostring() + " (" + operands.get(i).getClass().getSimpleName() + ')');
			}
            System.out.println("--------------------");
            return new DynamicError("Subtraction operation requires numeric operands.");
        }

        // Perform the subtraction
        result -= ((NumVal) nextValue).v();
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
	public Value visit(AST.FuncExp e, Env env) {
    String define = e.define();
    String name = e.name();
    List<Exp> body = e.body();

    // Handle function definition
    if ("def".equals(define)) {
        //System.out.println("Defining function: " + name);
        Value.FunVal function = new Value.FunVal(env, body); // Create a function value
        ((GlobalEnv) initEnv).extend(name, function); // Store the function in the global environment
        return function;
    }

    // Handle function call
    Value value = env.get(name); // Look up the function in the environment
    if (!(value instanceof Value.FunVal)) {
        return new Value.DynamicError("Function not defined: " + name);
    }

    Value.FunVal function = (Value.FunVal) value;
    Env functionEnv = function.env(); // Get the function's environment
    List<Exp> functionBody = function.bodyList();

    Value result = new Value.UnitVal(); // Default return value
    for (Exp exp : functionBody) {
        result = exp.accept(this, functionEnv); // Evaluate each expression in the body
    }
    return result;
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
		Value first = e.first_exp().accept(this, env);
		Value second = e.second_exp().accept(this, env);

		// Check if both operands are numbers
		if (first instanceof Value.NumVal && second instanceof Value.NumVal) {
			return new Value.BoolVal(((Value.NumVal) first).v() >= ((Value.NumVal) second).v());
		}

		// Check if both operands are strings
		if (first instanceof Value.StringVal && second instanceof Value.StringVal) {
			return new Value.BoolVal(((Value.StringVal) first).v().compareTo(((Value.StringVal) second).v()) >= 0);
		}

		// Handle type mismatch
		String firstType = first.getClass().getSimpleName();
		String secondType = second.getClass().getSimpleName();

		System.out.println("--------------------");
		System.out.println("Error: Comparison operation ('>=') requires numeric or string operands.");
		System.out.println("Expression causing the issue: " + 
			(e.first_exp() instanceof VarExp ? ((VarExp) e.first_exp()).name() : "unknown") + 
			" >= " + 
			(e.second_exp() instanceof VarExp ? ((VarExp) e.second_exp()).name() : "unknown"));
		System.out.println("First operand value: " + first.tostring() + " (type: " + firstType + ")");
		System.out.println("Second operand value: " + second.tostring() + " (type: " + secondType + ")");
		System.out.println("--------------------");

		// Return a dynamic error with a detailed message
		return new Value.DynamicError(
			"Comparison operation ('>=') requires numeric or string operands. " +
			"First operand type: " + firstType + ", " +
			"Second operand type: " + secondType
		);
	}

	
	@Override
	public Value visit(LessEqualExp e, Env env) { // New for funclang.
		Value first = e.first_exp().accept(this, env);
		Value second = e.second_exp().accept(this, env);

		// Check if both operands are numbers
		if (first instanceof Value.NumVal && second instanceof Value.NumVal) {
			return new Value.BoolVal(((Value.NumVal) first).v() <= ((Value.NumVal) second).v());
		}

		// Check if both operands are strings
		if (first instanceof Value.StringVal && second instanceof Value.StringVal) {
			return new Value.BoolVal(((Value.StringVal) first).v().compareTo(((Value.StringVal) second).v()) <= 0);
		}

		// Handle type mismatch
		String firstType = first.getClass().getSimpleName();
		String secondType = second.getClass().getSimpleName();

		System.out.println("--------------------");
		System.out.println("Error: Comparison operation ('<=') requires numeric or string operands.");
		System.out.println("Expression causing the issue: " + 
			(e.first_exp() instanceof VarExp ? ((VarExp) e.first_exp()).name() : "unknown") + 
			" <= " + 
			(e.second_exp() instanceof VarExp ? ((VarExp) e.second_exp()).name() : "unknown"));
		System.out.println("First operand value: " + first.tostring() + " (type: " + firstType + ")");
		System.out.println("Second operand value: " + second.tostring() + " (type: " + secondType + ")");
		System.out.println("--------------------");

		// Return a dynamic error with a detailed message
		return new Value.DynamicError(
			"Comparison operation ('<=') requires numeric or string operands. " +
			"First operand type: " + firstType + ", " +
			"Second operand type: " + secondType
		);
	}


	@Override
	public Value visit(GreaterExp e, Env env) { // New for funclang.
		Value first = e.first_exp().accept(this, env);
		Value second = e.second_exp().accept(this, env);

		// Check if both operands are numbers
		if (first instanceof Value.NumVal && second instanceof Value.NumVal) {
			return new Value.BoolVal(((Value.NumVal) first).v() > ((Value.NumVal) second).v());
		}

		// Check if both operands are strings
		if (first instanceof Value.StringVal && second instanceof Value.StringVal) {
			return new Value.BoolVal(((Value.StringVal) first).v().compareTo(((Value.StringVal) second).v()) > 0);
		}

		// Handle type mismatch
		String firstType = first.getClass().getSimpleName();
		String secondType = second.getClass().getSimpleName();

		System.out.println("--------------------");
		System.out.println("Error: Comparison operation ('>') requires numeric or string operands.");
		System.out.println("Expression causing the issue: " + 
			(e.first_exp() instanceof VarExp ? ((VarExp) e.first_exp()).name() : "unknown") + 
			" > " + 
			(e.second_exp() instanceof VarExp ? ((VarExp) e.second_exp()).name() : "unknown"));
		System.out.println("First operand value: " + first.tostring() + " (type: " + firstType + ")");
		System.out.println("Second operand value: " + second.tostring() + " (type: " + secondType + ")");
		System.out.println("--------------------");

		// Return a dynamic error with a detailed message
		return new Value.DynamicError(
			"Comparison operation ('>') requires numeric or string operands. " +
			"First operand type: " + firstType + ", " +
			"Second operand type: " + secondType
		);
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
				System.out.println("--------------------");
				System.out.println("Error: Condition must evaluate to a boolean for While Loop.");
				System.out.println("Expression causing the issue: while(" + e.condition().accept(this, env).tostring() + ") {...} (" + e.condition().getClass().getSimpleName() + ')');
				System.out.println("--------------------");
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
		// Handle the case where the condition is not a boolean
		System.out.println("--------------------");
		System.out.println("Error: Condition not a boolean in expression.");
		System.out.println("Expression causing the issue: if(" + e.conditional().accept(this, env).tostring() + ") {...} (" + e.conditional().getClass().getSimpleName() + ')');
		System.out.println("--------------------");
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
		System.out.println("--------------------");
		System.out.println("Error: Increment operation '++' requires a variable.");
		System.out.println("Expression causing the issue: " + e.fst().accept(this, env).tostring() + " (" +e.fst().getClass().getSimpleName() + ')');
		System.out.println("--------------------");
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

	// Handle non-numeric values
	System.out.println("--------------------");
	System.out.println("Error: Increment operation '++' is only valid for numbers.");
	System.out.println("Variable: " + varName);
	System.out.println("Current value: " + (value != null ? value.tostring() : "null"));
	System.out.println("Type: " + (value != null ? value.getClass().getSimpleName() : "null"));
	System.out.println("--------------------");

	// Return an error if the value is not numeric
	return new Value.DynamicError("Increment operation is only valid for numbers.");
}

@Override
public Value visit(AST.DeIncExp e, Env env) {
	// Ensure the expression is a variable
	if (!(e.fst() instanceof AST.VarExp)) {
		System.out.println("--------------------");
		System.out.println("Error: Decrement operation '--' requires a variable.");
		System.out.println("Expression causing the issue: " + e.fst().accept(this, env).tostring() + " (" + e.fst().getClass().getSimpleName() + ')');
		System.out.println("--------------------");
		return new Value.DynamicError("Decrement operation requires a variable.");
	}

	// Get the variable name
	String varName = ((AST.VarExp) e.fst()).name();

	// Retrieve the current value of the variable
	Value value = env.get(varName);

	// Ensure the value is numeric
	if (value instanceof NumVal) {
		double decrementedValue = ((NumVal) value).v() - 1; // Decrement the numeric value

		// Update the variable in the environment
		((GlobalEnv) env).extend(varName, new NumVal(decrementedValue));

		// Return the decremented value
		return new NumVal(decrementedValue);
	}


// @Override
// public Value visit(AST.NotExp e, Env env) {
// 	// Ensure the expression is a variable
// 	Value value = e.fst().accept(this, env);
// 	if (!(e.fst() instanceof AST.BoolExp)) {
// 		System.out.println("--------------------");
// 		System.out.println("Error: Must be a boolean expression for 'not' operation.");
// 		System.out.println("Expression causing the issue: " + e.fst().accept(this, env).tostring() + " (" + e.fst().getClass().getSimpleName() + ')');
// 		System.out.println("Current value: " + (e.fst() != null ? e.fst().accept(this, env).tostring() : "null"));
// 		System.out.println("--------------------");
// 		return new Value.DynamicError("Not operation requires a boolean expression.");
// 	}
// }



	// Handle non-numeric values
	System.out.println("--------------------");
	System.out.println("Error: Decrement operation '--' is only valid for numbers.");
	System.out.println("Variable: " + varName);
	System.out.println("Current value: " + (value != null ? value.tostring() : "null"));
	System.out.println("Type: " + (value != null ? value.getClass().getSimpleName() : "null"));
	System.out.println("--------------------");

	// Return an error if the value is not numeric
	return new Value.DynamicError("Decrement operation is only valid for numbers.");
}

@Override
public Value visit(AST.RandomExp e, Env env) {
    // Evaluate the `min` expression
    Value minValue = e.min().accept(this, env);

    // Ensure `min` is numeric
    if (!(minValue instanceof NumVal)) {
        return new DynamicError("RandomExp requires a numeric value for min.");
    }

    double min = ((NumVal) minValue).v();

    // If `max` is null, treat it as a single argument case
    double max = min;
    if (e.max() != null) {
        // Evaluate the `max` expression
        Value maxValue = e.max().accept(this, env);

        // Ensure `max` is numeric
        if (!(maxValue instanceof NumVal)) {
			System.out.println("--------------------");
			System.out.println("Error: RandomExp requires a numeric value for max.");
			System.out.println("Expression causing the issue: " + e.max().accept(this, env).tostring() + " (" + e.max().getClass().getSimpleName() + ')');
			System.out.println("--------------------");
            return new DynamicError("RandomExp requires a numeric value for max.");
        }

        max = ((NumVal) maxValue).v();
    }

    // Ensure `min <= max`
    if (min > max) {
        return new DynamicError("RandomExp requires min <= max.");
    }

    // Generate a random integer between `min` and `max` (inclusive)
    int randomInt = (int) (Math.random() * (max - min + 1) + min);

    // Return the random integer as a double
    return new NumVal((double) randomInt);
}

// @Override
// public Value visit(AST.Concat e, Env env) {
//     // Evaluate all string expressions in the list
//     List<Exp> stringExpressions = e.strs();
//     StringBuilder concatenatedString = new StringBuilder();

//     for (Exp exp : stringExpressions) {
//         Value value = exp.accept(this, env);

//         // Ensure each operand is a string
//         if (!(value instanceof Value.StringVal)) {
//             String valueType = value.getClass().getSimpleName();
//             System.out.println("--------------------");
//             System.out.println("Error: Concatenation operation requires string operands.");
//             System.out.println("Expression causing the issue: " + 
//                 (exp instanceof VarExp ? ((VarExp) exp).name() : "unknown"));
//             System.out.println("Value: " + value.tostring() + " (type: " + valueType + ")");
//             System.out.println("--------------------");

//             // Return a dynamic error with a detailed message
//             return new Value.DynamicError(
//                 "Concatenation operation requires string operands. " +
//                 "Found operand of type: " + valueType
//             );
//         }

//         // Append the string value to the result
//         concatenatedString.append(((Value.StringVal) value).v());
//     }

//     // Return the concatenated string as a Value.StringVal
//     return new Value.StringVal(concatenatedString.toString());
// }

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
		if (System.getProperty("debugMode") != null && System.getProperty("debugMode").equalsIgnoreCase("true")) {
			System.out.println("The userInput contains characters.");
			System.out.flush(); // Flush the output to ensure it appears immediately
		}
		return new Value.StringVal(userInput);
	}
	else if (userInput.matches(".*[0-9].*")) {
		if (System.getProperty("debugMode") != null && System.getProperty("debugMode").equalsIgnoreCase("true")) {
		System.out.println("The userInput contains numbers.");
		System.out.flush(); // Flush the output to ensure it appears immediately
		}
		return new Value.NumVal(Double.parseDouble(userInput));
	}
	else if (userInput.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
		if (System.getProperty("debugMode") != null && System.getProperty("debugMode").equalsIgnoreCase("true")) {
		System.out.println("The userInput contains special characters.");
		System.out.flush(); // Flush the output to ensure it appears immediately
		}
		return new Value.StringVal(userInput);
	}
	else {
		if (System.getProperty("debugMode") != null && System.getProperty("debugMode").equalsIgnoreCase("true")) {
		System.out.println("The userInput is empty or contains no valid characters.");
		System.out.flush(); // Flush the output to ensure it appears immediately
		}
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
