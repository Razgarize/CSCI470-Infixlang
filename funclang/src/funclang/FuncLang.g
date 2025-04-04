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
	| val=num_or_str { $ast = $val.ast; }
    | com=comexp { $ast = $com.ast; }
    | comp=compexp { $ast = $comp.ast; }
    | id = Identifier '=' e=exp { $ast = new DefineDecl($id.text, $e.ast); }
    | wl=whileexp { $ast = $wl.ast; }
    |  a = arithexp { $ast = $a.ast; }
	;


whileexp returns [WhileExp ast]
    : While '('  condition=exp ')' '{' body=exp '}'
      { $ast = new WhileExp($condition.ast, $body.ast); }
    ;

arithexp returns [Exp ast]
    locals [ArrayList<Exp> list]:
      al = arithexp '+' ar = term {
                        $list = new ArrayList<Exp>();
                        $list.add($al.ast);
                        $list.add($ar.ast);
                        $ast = new AddExp($list);
                 }
    | sl = arithexp '-' sr = term {
                        $list = new ArrayList<Exp>();
                        $list.add($sl.ast);
                        $list.add($sr.ast);
                        $ast = new SubExp($list);
                 }
    | t = term { $ast = $t.ast; }
    ;
term returns [Exp ast]
    locals [ArrayList<Exp> list]:
      ml = term '*' mr = factor {
                    $list = new ArrayList<Exp>();
                    $list.add($ml.ast);
                    $list.add($mr.ast);
                    $ast = new MultExp($list);
                 }
    | dl = term '/' dr = factor {
                    $list = new ArrayList<Exp>();
                    $list.add($dl.ast);
                    $list.add($dr.ast);
                    $ast = new DivExp($list);
                 }
    | cl = term '%' cr = factor {
                    $list = new ArrayList<Exp>();
                    $list.add($cl.ast);
                    $list.add($cr.ast);
                    $ast = new ModExp($list);
                 }
    | f = factor { $ast = $f.ast; }
    ;

factor returns [Exp ast]
    locals [ArrayList<Exp> list]:
    e = exponent { $ast = $e.ast; }
    ;
exponent returns [Exp ast]:
    n = numexp     { $ast = $n.ast; }
    | v = varexp { $ast = new VarExp($v.text); }
    | '(' e = exp ')' { $ast = $e.ast; }
    ;

compexp returns [Exp ast] : 
     e1=compexp '==' e2=num_or_str { $ast = new EqualExp($e1.ast, $e2.ast); } 
    | e1=compexp '>' e2=num_or_str { $ast = new GreaterExp($e1.ast, $e2.ast); } 
    | e1=compexp '<' e2=num_or_str { $ast = new LessExp($e1.ast, $e2.ast); }
    | t= num_or_str { $ast = $t.ast; }
    ;

// Comments and Multi-line comments.
comexp returns [Exp ast] : 
    Line_Comment { $ast = new UnitExp(); }
    | Comment { $ast = new UnitExp(); }
    ;

num_or_str returns [Exp ast]: 
    num=numexp { $ast = $num.ast; }
    | str=strexp { $ast = $str.ast; }
    | v = varexp { $ast = new VarExp($v.text); }
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
While : 'while' ;

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
