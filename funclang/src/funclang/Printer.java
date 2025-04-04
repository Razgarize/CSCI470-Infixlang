package funclang;

import java.util.List;

import funclang.AST.Exp;

public class Printer {
	public void print(Value v) {
		if(v.tostring() != "")
			//System.out.println(v.tostring());
			return;
	}
	public void print(Exception e) {
		//System.out.println(e.toString());
		return;
	}
	
	public static class Formatter implements AST.Visitor<String> {
		
		public String visit(AST.AddExp e, Env env) {
			return "";
		}
		
		public String visit(AST.UnitExp e, Env env) {
			return "unit";
		}

		public String visit(AST.NumExp e, Env env) {
			return "";
		}
		
		public String visit(AST.StrExp e, Env env) {
			return "";
		}
		
		public String visit(AST.BoolExp e, Env env) {
			return "";
		}

		public String visit(AST.DivExp e, Env env) {
			return "";
		}
				
		public String visit(AST.ReadExp e, Env env) {
			return "";
		}

		public String visit(AST.EvalExp e, Env env) {
			return "";
		}

		public String visit(AST.MultExp e, Env env) {
			return "";
		}

		public String visit(AST.ModExp e, Env env) {
			return "";
		}
		
		public String visit(AST.Program p, Env env) {
			return "";
		}
		
		public String visit(AST.SubExp e, Env env) {
			return "";
		}
		
		public String visit(AST.VarExp e, Env env) {
			return "";
		}
		
		public String visit(AST.LetExp e, Env env) {
			return "";
		}
		
		public String visit(AST.PrintExp e, Env env) {
			return "";
		}

		public String visit(AST.DefineDecl d, Env env) {
			return "";
		}
		
		public String visit(AST.LambdaExp e, Env env) {
			return "";
		}
		
		public String visit(AST.CallExp e, Env env) {
			return "";
		}
		
		public String visit(AST.IfExp e, Env env) {
			return "";
		}
		
		public String visit(AST.LessExp e, Env env) {
			return "";
		}

		public String visit(AST.EqualExp e, Env env) {
			return "";
		}
		
		public String visit(AST.GreaterExp e, Env env) {
			return "";
		}
		
		public String visit(AST.CarExp e, Env env) {
			return "";
		}
		
		public String visit(AST.CdrExp e, Env env) {
			return "";
		}
		
		public String visit(AST.ConsExp e, Env env) {
			return "";
		}
		
		public String visit(AST.ListExp e, Env env) {
			return "";
		}



		public String visit(AST.NullExp e, Env env) {
			return "";
		}

		
		public String visit(AST.WhileExp e, Env env) {
			return "";
		}
		
	}
}
