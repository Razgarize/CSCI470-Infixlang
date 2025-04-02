grammar FuncLang;

program returns [Program ast]        
	locals [ArrayList<DefineDecl> defs, Exp expr]
	@init { $defs = new ArrayList<DefineDecl>(); $expr = new UnitExp(); } :
	(def=definedecl { $defs.add($def.ast); } )* (e=exp { $expr = $e.ast; } )? 
	{ $ast = new Program($defs, $expr); }
	;

definedecl returns [DefineDecl ast] :
	    Define 
		id=Identifier
        '{'e=exp'}' 
        { $ast = new DefineDecl($id.text, $e.ast); }
	;

exp returns [Exp ast]: 
	va=varexp { $ast = $va.ast; }
    | bl=boolexp { $ast = $bl.ast; }
    | eq=equalexp { $ast = $eq.ast; }
    | gt=greaterexp { $ast = $gt.ast; }
    | less=lessexp { $ast = $less.ast; }
	| val=num_or_str { $ast = $val.ast; }
	;



num_or_str returns [Exp ast]: 
    num=numexp { $ast = $num.ast; }
    | str=strexp { $ast = $str.ast; }
    ;

lessexp returns [LessExp ast] : 
    e1=num_or_str '<' e2=num_or_str { $ast = new LessExp($e1.ast, $e2.ast); } 
    ;
    
greaterexp returns [GreaterExp ast] : 
    e1=num_or_str '>' e2=num_or_str { $ast = new GreaterExp($e1.ast, $e2.ast); } 
    ;

equalexp returns [EqualExp ast] : 
    e1=num_or_str '==' e2=num_or_str { $ast = new EqualExp($e1.ast, $e2.ast); } 
    ;

boolexp returns [BoolExp ast] : 
    TrueLiteral { $ast = new BoolExp(true); } 
    | FalseLiteral { $ast = new BoolExp(false); }
    ;

strexp returns [StrExp ast] :
    StrLiteral { $ast = new StrExp($StrLiteral.text); }
    ;
callexp returns [CallExp ast] 
	locals [ArrayList<Exp> arguments = new ArrayList<Exp>();  ] :
	 f=exp'(' 
		( e=exp { $arguments.add($e.ast); } )* 
	')' { $ast = new CallExp($f.ast,$arguments); }
	;

numexp returns [NumExp ast]:
	n0=Number { $ast = new NumExp(Integer.parseInt($n0.text)); } 
	| '-' n0=Number { $ast = new NumExp(-Integer.parseInt($n0.text)); }
	| n0=Number Dot n1=Number { $ast = new NumExp(Double.parseDouble($n0.text+"."+$n1.text)); }
	| '-' n0=Number Dot n1=Number { $ast = new NumExp(Double.parseDouble("-" + $n0.text+"."+$n1.text)); }
	;        

varexp returns [VarExp ast]: 
	id=Identifier { $ast = new VarExp($id.text); }
	;

// Lexical Specification of this Programming Language
Define : 'def' ;
Let : 'let' ;
Dot : '.' ;
Lambda : 'lambda' ;
If : 'if' ; 
Car : 'car' ; 
Cdr : 'cdr' ; 
Cons : 'cons' ; 
List : 'list' ; 
Null : 'null?' ; 
Less : '<' ;
Equal : '=' ;
Greater : '>' ;
TrueLiteral : 'True' ;
FalseLiteral : 'False' ;

Number : DIGIT+ ;

Identifier :   Letter LetterOrDigit*;

Letter :   [a-zA-Z$_]
	|   ~[\u0000-\u00FF\uD800-\uDBFF] 
		{Character.isJavaIdentifierStart(_input.LA(-1))}?
	|   [\uD800-\uDBFF] [\uDC00-\uDFFF] 
		{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}? ;

LetterOrDigit: [a-zA-Z0-9$_]
	|   ~[\u0000-\u00FF\uD800-\uDBFF] 
		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|    [\uD800-\uDBFF] [\uDC00-\uDFFF] 
		{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?;

fragment DIGIT: ('0'..'9');

AT : '@';
ELLIPSIS : '...';
WS  :  [ \t\r\n\u000C]+ -> skip;
Comment :   '/*' .*? '*/' -> skip;
Line_Comment :   '//' ~[\r\n]* -> skip;

fragment ESCQUOTE : '\\"';
StrLiteral :   '"' ( ESCQUOTE | ~('\n'|'\r') )*? '"';
