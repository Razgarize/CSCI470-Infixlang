grammar ArithLang;

 program returns [Program ast] :   
		e=exp { $ast = new Program($e.ast); }
		;

exp returns [Exp ast]:
 id = Identifier '=' e=exp { $ast = new AssignExp($id.text, $e.ast); }
 | a = arithexp { $ast = $a.ast; }
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
| f = factor { $ast = $f.ast; }
;

factor returns [Exp ast]
	locals [ArrayList<Exp> list]:
	  el = factor '**' er = exponent {
					$list = new ArrayList<Exp>();
					$list.add($el.ast);
					$list.add($er.ast);
					$ast = new PowExp($list);
				 }
| e = exponent { $ast = $e.ast; }
;

exponent returns [Exp ast]:
  n = numexp     { $ast = $n.ast; }
| v = varexp { $ast = new VarExp($v.text); }
| '-' neg = exp { $ast = new NegExp($neg.ast); }
| '(' e = exp ')' { $ast = $e.ast; }
;

 numexp returns [NumExp ast]:
 		n0=Number { $ast = new NumExp(Integer.parseInt($n0.text)); } 
  		| '-' n0=Number { $ast = new NumExp(-Integer.parseInt($n0.text)); }
  		| n0=Number Dot n1=Number { $ast = new NumExp(Double.parseDouble($n0.text+"."+$n1.text)); }
  		| '-' n0=Number Dot n1=Number { $ast = new NumExp(Double.parseDouble("-" + $n0.text+"."+$n1.text)); }
  		;		

varexp returns [Exp ast] : 
    id=Identifier { $ast = new VarExp($id.text); }          // Variable case (like `a`, `b`)
    | n=numexp { $ast = $n.ast; }                            // Numeric case (like `3`, `-5`)
    ;

 // Lexical Specification of this Programming Language
 //  - lexical specification rules start with uppercase
 
 Dot : '.' ;

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



