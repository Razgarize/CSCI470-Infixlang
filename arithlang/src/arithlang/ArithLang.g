grammar ArithLang;

program returns [Program ast]:   
    e=exp { $ast = new Program($e.ast); }
    ;

exp returns [Exp ast]: 
    n=numexp { $ast = $n.ast; }
    | a=addexp { $ast = $a.ast; }
    | s=subexp { $ast = $s.ast; }
    | m=multexp { $ast = $m.ast; }
    | d=divexp { $ast = $d.ast; }
    | p=powexp { $ast = $p.ast; }
    | neg=negexp { $ast = $neg.ast; }
    | in=infixaddsubt { $ast = $in.ast; }
    ;

numexp returns [NumExp ast]:
    n=Number { $ast = new NumExp(Integer.parseInt($n.text)); } 
    | f=FLOAT { $ast = new NumExp(Double.parseDouble($f.text)); }
    | '-' n=Number { $ast = new NumExp(-Integer.parseInt($n.text)); }
    | '-' f=FLOAT { $ast = new NumExp(Double.parseDouble("-" + $f.text)); }
    ;

addexp returns [AddExp ast]
    locals [ArrayList<Exp> list]
    @init { $list = new ArrayList<Exp>(); } :
    '(' '+' e=exp { $list.add($e.ast); } (e=exp { $list.add($e.ast); })+ ')' 
    { $ast = new AddExp($list); }
    ;

subexp returns [SubExp ast]  
    locals [ArrayList<Exp> list]
    @init { $list = new ArrayList<Exp>(); } :
    '(' '-' e=exp { $list.add($e.ast); } (e=exp { $list.add($e.ast); })+ ')' 
    { $ast = new SubExp($list); }
    ;

multexp returns [MultExp ast] 
    locals [ArrayList<Exp> list]
    @init { $list = new ArrayList<Exp>(); } :
    '(' '*' e=exp { $list.add($e.ast); } (e=exp { $list.add($e.ast); })+ ')' 
    { $ast = new MultExp($list); }
    ;

divexp returns [DivExp ast] 
    locals [ArrayList<Exp> list]
    @init { $list = new ArrayList<Exp>(); } :
    '(' '/' e=exp { $list.add($e.ast); } (e=exp { $list.add($e.ast); })+ ')' 
    { $ast = new DivExp($list); }
    ;

powexp returns [PowExp ast]
    locals [ArrayList<Exp> list]
    @init { $list = new ArrayList<Exp>(); } :
    '(' '**' e=exp { $list.add($e.ast); } (e=exp { $list.add($e.ast); })+ ')' 
    { $ast = new PowExp($list); }
    ;

negexp returns [NegExp ast]:
    '(' '-' e=exp ')' { $ast=new NegExp($e.ast); }
    ;

infixaddsubt returns [Exp ast]
    @init { $ast = null; ArrayList<Exp> list = new ArrayList<Exp>(); }:
    l=infixmuldiv { $ast = $l.ast; }
    (('+' r=infixmuldiv { 
        list = new ArrayList<Exp>(); list.add($ast); list.add($r.ast);
        $ast = new AddExp(list);
    })
    | ('-' r=infixmuldiv { 
        list = new ArrayList<Exp>(); list.add($ast); list.add($r.ast);
        $ast = new SubExp(list);
    })
    )*
    ;

infixmuldiv returns [Exp ast]
    @init { $ast = null; ArrayList<Exp> list = new ArrayList<Exp>(); }:
    l=infixpower { $ast = $l.ast; } 
    (('*' r=infixpower { 
        list = new ArrayList<Exp>(); list.add($ast); list.add($r.ast);
        $ast = new MultExp(list);
    }) 
    | ('/' r=infixpower { 
        list = new ArrayList<Exp>(); list.add($ast); list.add($r.ast);
        $ast = new DivExp(list);
    })
    | r=infixpower {  // Implicit multiplication (e.g., "3(2)" -> "3 * 2")
        list = new ArrayList<Exp>(); list.add($ast); list.add($r.ast);
        $ast = new MultExp(list);
    }
    )*
    ;

infixpower returns [Exp ast]:
    '(' e=exp ')' { $ast = $e.ast; }
    | base=numexp { $ast = $base.ast; }
    ('**' exponent=infixpower {  
        ArrayList<Exp> list = new ArrayList<Exp>();
        list.add($ast);
        list.add($exponent.ast);
        $ast = new PowExp(list);
    })?
    ;

Dot : '.' ;

FLOAT : DIGIT+ '.' DIGIT+ ;

Number : DIGIT+ ;

Identifier : Letter LetterOrDigit*;

Letter : [a-zA-Z$_]
    | ~[\u0000-\u00FF\uD800-\uDBFF] {Character.isJavaIdentifierStart(_input.LA(-1))}?
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

LetterOrDigit: [a-zA-Z0-9$_]
    | ~[\u0000-\u00FF\uD800-\uDBFF] {Character.isJavaIdentifierPart(_input.LA(-1))}?
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment DIGIT: ('0'..'9');

AT : '@';
ELLIPSIS : '...';
WS  : [ \t\r\n\u000C]+ -> skip;
Comment : '/*' .*? '*/' -> skip;
Line_Comment : '//' ~[\r\n]* -> skip;
