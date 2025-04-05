grammar FuncLang;

// Program structure
program returns [Program ast]
    locals [ArrayList<DefineDecl> defs, ArrayList<Exp> expr]
    @init { $defs = new ArrayList<DefineDecl>(); $expr = new ArrayList<Exp>(); } :
    ((def=definedecl { $defs.add($def.ast); }) | (e=statement { $expr.add($e.ast); }))*
    { $ast = new Program($defs, $expr); }
    ;

// Define declarations
definedecl returns [DefineDecl ast] :
    Define id=Identifier '{' e=exp '}'
    { $ast = new DefineDecl($id.text, $e.ast); }
    ;

// functiondecl returns [FunctionDecl ast] :
//     'def' id=Identifier '{' body=exp '}' 
//     { $ast = new FunctionDecl($id.text, $body.ast); }
//     ;

statement returns [Exp ast] :
    e=exp { $ast = $e.ast; }
    | wl=whileexp { $ast = $wl.ast; }
    | com=comexp { $ast = $com.ast; }
    ;

// Expressions
exp returns [Exp ast] :
    va=varexp { $ast = $va.ast; }
    | bl=boolexp { $ast = $bl.ast; }
    | val=num_or_str { $ast = $val.ast; }
    | comp=compexp { $ast = $comp.ast; }
    | id=Identifier '=' e=exp { $ast = new DefineDecl($id.text, $e.ast); } 
    | pr=printexp { $ast = $pr.ast; }
    | a=arithexp { $ast = $a.ast; }
    ; //| id=Identifier '=' 'input' '(' e=userinput ')' { $ast = new DefineDecl($id.text, $e.ast); }

// userinput returns [Exp ast] :
//     e=exp
//     { $ast = new UserInputExp($e.ast); }
//     ;

// Print expression
printexp returns [PrintExp ast] :
    Print '(' e=exp ')'
    { $ast = new PrintExp($e.ast); }
    ;

// While expression
whileexp returns [Exp ast]
    locals [ArrayList<Exp> bodies]
    @init { $bodies = new ArrayList<Exp>(); } :
    While '(' condition=exp ')' '{'
    (body=exp { $bodies.add($body.ast); })*
    '}'
    { $ast = new WhileExp($condition.ast, $bodies); }
    ;



// Arithmetic expressions
arithexp returns [Exp ast]
    locals [ArrayList<Exp> list] :
    al=arithexp '+' ar=term {
        $list = new ArrayList<Exp>();
        $list.add($al.ast);
        $list.add($ar.ast);
        $ast = new AddExp($list);
    }
    | sl=arithexp '-' sr=term {
        $list = new ArrayList<Exp>();
        $list.add($sl.ast);
        $list.add($sr.ast);
        $ast = new SubExp($list);
    }
    | t=term { $ast = $t.ast; }
    ;

// Term expressions
term returns [Exp ast]
    locals [ArrayList<Exp> list] :
    ml=term '*' mr=factor {
        $list = new ArrayList<Exp>();
        $list.add($ml.ast);
        $list.add($mr.ast);
        $ast = new MultExp($list);
    }
    | dl=term '/' dr=factor {
        $list = new ArrayList<Exp>();
        $list.add($dl.ast);
        $list.add($dr.ast);
        $ast = new DivExp($list);
    }
    | cl=term '%' cr=factor {
        $list = new ArrayList<Exp>();
        $list.add($cl.ast);
        $list.add($cr.ast);
        $ast = new ModExp($list);
    }
    | f=factor { $ast = $f.ast; }
    ;

// Factor expressions
factor returns [Exp ast] :
    e=exponent { $ast = $e.ast; }
    ;

// Exponent expressions
exponent returns [Exp ast] :
    n=numexp { $ast = $n.ast; }
    | v=varexp { $ast = new VarExp($v.text); }
    | '(' e=exp ')' { $ast = $e.ast; }
    ;

// Comparison expressions
compexp returns [Exp ast] :
    e1=compexp '==' e2=num_or_str { $ast = new EqualExp($e1.ast, $e2.ast); }
    | e1=compexp '>' e2=num_or_str { $ast = new GreaterExp($e1.ast, $e2.ast); }
    | e1=compexp '<' e2=num_or_str { $ast = new LessExp($e1.ast, $e2.ast); }
    | e1=compexp '!=' e2=num_or_str { $ast = new NotEqualExp($e1.ast, $e2.ast); }
    | t=num_or_str { $ast = $t.ast; }
    ;

// Comments
comexp returns [Exp ast] :
    Line_Comment { $ast = new UnitExp(); }
    | Comment { $ast = new UnitExp(); }
    ;

// Numeric or string expressions
num_or_str returns [Exp ast] :
    num=numexp { $ast = $num.ast; }
    | str=strexp { $ast = $str.ast; }
    | v=varexp { $ast = new VarExp($v.text); }
    ;

// Boolean expressions
boolexp returns [BoolExp ast] :
    TrueLiteral { $ast = new BoolExp(true); }
    | FalseLiteral { $ast = new BoolExp(false); }
    ;

// String expressions
strexp returns [StrExp ast] :
    StrLiteral { $ast = new StrExp($StrLiteral.text); }
    ;

// Function call expressions
callexp returns [CallExp ast]
    locals [ArrayList<Exp> arguments = new ArrayList<Exp>();] :
    f=exp '('
    (e=exp { $arguments.add($e.ast); })*
    ')'
    { $ast = new CallExp($f.ast, $arguments); }
    ;

// Numeric expressions
numexp returns [NumExp ast] :
    n0=Number { $ast = new NumExp(Integer.parseInt($n0.text)); }
    | '-' n0=Number { $ast = new NumExp(-Integer.parseInt($n0.text)); }
    | n0=Number Dot n1=Number { $ast = new NumExp(Double.parseDouble($n0.text + "." + $n1.text)); }
    | '-' n0=Number Dot n1=Number { $ast = new NumExp(Double.parseDouble("-" + $n0.text + "." + $n1.text)); }
    ;

// Variable expressions
varexp returns [VarExp ast] :
    id=Identifier { $ast = new VarExp($id.text); }
     ;



// Lexical rules
Define : 'def';
Let : 'let';
Dot : '.';
Lambda : 'lambda';
If : 'if';
Car : 'car';
Cdr : 'cdr';
Cons : 'cons';
List : 'list';
Null : 'null?';
Less : '<';
Equal : '=';
Greater : '>';
TrueLiteral : 'True';
FalseLiteral : 'False';
While : 'while';
Print : 'print';

Number : DIGIT+;
Identifier : Letter LetterOrDigit*;

Letter : [a-zA-Z$_]
    | ~[\u0000-\u00FF\uD800-\uDBFF] {Character.isJavaIdentifierStart(_input.LA(-1))}?
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?;

LetterOrDigit : [a-zA-Z0-9$_]
    | ~[\u0000-\u00FF\uD800-\uDBFF] {Character.isJavaIdentifierPart(_input.LA(-1))}?
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?;

fragment DIGIT : ('0'..'9');

AT : '@';
ELLIPSIS : '...';
WS : [ \t\r\n\u000C]+ -> skip;
Comment : '/*' ( ((Letter) (NL)? | (LetterOrDigit) (NL)? | (Number) (NL)?)* )? '*/' -> skip;
Line_Comment : '//' ~[\r\n]* -> skip;
NL : [\n] -> skip;
fragment ESCQUOTE : '\\"';
StrLiteral : '"' (ESCQUOTE | ~('\n' | '\r'))*? '"';
