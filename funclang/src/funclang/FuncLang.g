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
    'static def' id=Identifier '{' e=exp '}'
    { $ast = new DefineDecl($id.text, $e.ast); }
    ;

// functiondecl returns [FunctionDecl ast] :
//     'def' id=Identifier '{' body=exp '}' 
//     { $ast = new FunctionDecl($id.text, $body.ast); }
//     ;

statement returns [Exp ast] :
    e=exp { $ast = $e.ast; }
    | func=functionexp { $ast = $func.ast; }
    ;

functionexp returns [FuncExp ast]
    locals [ArrayList<Exp> bodies]
    @init { $bodies = new ArrayList<Exp>(); } :
    def=Define id=Identifier '()' '{' (body=statement { $bodies.add($body.ast); })* '}'
    { $ast = new FuncExp($def.text, $id.text, $bodies); } // Function definition
    | id=Identifier '()'
    { $ast = new FuncExp(null, $id.text, null); } // Function call
    ;

// Function call
funccall returns [FuncExp ast]
    locals [ArrayList<Exp> bodies]
    @init { $bodies = new ArrayList<Exp>(); } :
    id=Identifier '()'
    { $ast = new FuncExp(null, $id.text, $bodies); } // Function call
    ;

// Expressions
exp returns [Exp ast] :
    va=varexp { $ast = $va.ast; }
    | wl=whileexp { $ast = $wl.ast; }
    | com=comexp { $ast = $com.ast; }
    | pr=printexp { $ast = $pr.ast; }
    | ife=ifexp { $ast = $ife.ast; }
    | bl=boolexp { $ast = $bl.ast; }
    | val=num_or_str { $ast = $val.ast; }
    | comp=compexp { $ast = $comp.ast; }
    | func=funccall { $ast = $func.ast; }
    | id=Identifier '=' e=exp { $ast = new DefineDecl($id.text, $e.ast); } 
    | log=logexp { $ast = $log.ast; }
    | random=randomexp { $ast = $random.ast; }
    | input=inputexp { $ast = $input.ast; }
    | a=arithexp { $ast = $a.ast; }
    ; 


// Concatenation expression
// concat returns [Exp ast] 
//     locals [ArrayList<Exp> list]
//     @init { $list = new ArrayList<Exp>(); } :
//     base=num_or_str ( '&+' next=num_or_str { $list.add($base.ast); $list.add($next.ast); $ast = new Concat($list); } )*
//     ;

randomexp returns [RandomExp ast] :
     'random' '(' e1=exp ',' e2=exp ')' { $ast = new RandomExp($e1.ast, $e2.ast); }
    ;

// Print expression
printexp returns [PrintExp ast]
    locals [ArrayList<Exp> exps = new ArrayList<Exp>();] :
    Print '(' e=exp { $exps.add($e.ast); }
    (',' e=exp { $exps.add($e.ast); })* ')'
    { $ast = new PrintExp($exps); }
    ;

// While expression
whileexp returns [WhileExp ast]
    locals [ArrayList<Exp> bodies]
    @init { $bodies = new ArrayList<Exp>(); } :
    While '(' condition=exp ')' '{'
    (body=exp { $bodies.add($body.ast); })*
    '}'
    { $ast = new WhileExp($condition.ast, $bodies); }
    ;

ifexp returns [IfExp ast]
    locals [ArrayList<Exp> thenBodies, ArrayList<Exp> elseBodies]
    @init { $thenBodies = new ArrayList<Exp>(); $elseBodies = new ArrayList<Exp>(); } :
    If '(' condition=exp ')' '{'
    (thenBody=exp { $thenBodies.add($thenBody.ast); })*
    '}'
    (Else '{'
    (elseBody=exp { $elseBodies.add($elseBody.ast); })*
    '}')?
    { $ast = new IfExp($condition.ast, $thenBodies, $elseBodies); }
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
    | inc=exponent '++' {
        $ast = new IncExp($inc.ast);
    }
    | inc=exponent '--' {
    $ast = new DeIncExp($inc.ast);
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
    e1=num_or_str ('==' e2=num_or_str { $ast = new EqualExp($e1.ast, $e2.ast); }
                  | '>' e2=num_or_str { $ast = new GreaterExp($e1.ast, $e2.ast); }
                  | '<' e2=num_or_str { $ast = new LessExp($e1.ast, $e2.ast); }
                  | '!=' e2=num_or_str { $ast = new NotEqualExp($e1.ast, $e2.ast); }
                  | '<=' e2=num_or_str { $ast = new LessEqualExp($e1.ast, $e2.ast); }
                  | '>=' e2=num_or_str { $ast = new GreaterEqualExp($e1.ast, $e2.ast); })?
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
    | bl=boolexp { $ast = $bl.ast; }
    | '(' e=exp ')' { $ast = $e.ast; }
    | a=arithexp { $ast = $a.ast; }
    ;

// Boolean expressions
boolexp returns [BoolExp ast] :
    TrueLiteral { $ast = new BoolExp(true); }
    | FalseLiteral { $ast = new BoolExp(false); }
    | '(' e=boolexp ')' { $ast = $e.ast; }
    ;

// Logical expressions
logexp returns [Exp ast]
    : left=logexp ('&&' | 'and') right=compexp { $ast = new AndExp($left.ast, $right.ast); }
    | left=logexp ('||' | 'or') right=compexp { $ast = new OrExp($left.ast, $right.ast); }
    | compexp { $ast = $compexp.ast; }
    ;

// String expressions
strexp returns [StrExp ast] :
    StrLiteral { $ast = new StrExp($StrLiteral.text); }
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

inputexp returns [InputExp ast] :
    'input' '('  ( prompt=strexp ) ')' { $ast = new InputExp($prompt.ast); }
    | 'input' '()' { $ast = new InputExp(); }
    ;

// Lexical rules
Define : 'def';
Let : 'let';
Dot : '.';
Lambda : 'lambda';
If : 'if';
Else : 'else';
Car : 'car';
Cdr : 'cdr';
Cons : 'cons';
List : 'list';
Null : 'null?';
Less : '<';
Equal : '=';
Greater : '>';
TrueLiteral : 'true';
FalseLiteral : 'false';
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
