# PyC

Combining the Best of Python and C in a Rushed Way Where Everything is Global.

## Introduction

### Philosophy

PyC was designed to be an easy-to-implement language that merges the syntax of Python and C. We wanted to bring Python’s dynamic variable binding together with C-style scoping, removing Python’s reliance on whitespace.

### Influences

The language is primarily influenced by:

-   **Python**: Dynamic typing and flexible variable binding.
    
-   **C**: Curly brace `{}` scoping for structure, reducing indentation concerns.
    

## EBNF Description (Work in Progress)

```
program ::= exp | definedecl ;

exp ::= varexp 
		| boolexp 
		| num_or_str 
		| comments 
		| compexp
	    | Identifier '=' exp | arithexp ;

definedecl ::= Define Identifier '{' exp '}' ;

arithexp ::= term 
		| arithexp '+' term 
		| arithexp '-' term ;

term ::= factor 
		| term '*' factor 
		| term '/' factor ;

factor ::= exponent ; // Might change later

exponent ::= numexp 
		| varexp 
		| '(' exp ')' ;

// compexp needs reworking to support (4+4) > 3
// Currently only supports 4 > 5
compexp ::= num_or_str 
		| compexp '==' num_or_str
        | compexp '>' num_or_str | compexp '<' num_or_str ;

comexp ::= Line_Comment 
		| Multi_Line_Comment ;

boolexp ::= TrueLiteral 
		| FalseLiteral ;

numexp ::= Number 
		| '-' Number 
		| Number Dot Number 
		| '-' Number Dot Number ;

varexp ::= Identifier ;

strexp ::= StrLiteral ;

// Lexical rules
Define ::= 'def' ;
Dot ::= '.' ;
If ::= 'if' ;
TrueLiteral ::= 'True' ;
FalseLiteral ::= 'False' ;
While ::= 'while' ;

Number ::= Digit+ ;
Identifier ::= Letter LetterOrDigit* ;
Letter ::= [a-zA-Z$_] | ~[\u0000-\u00FF\uD800-\uDBFF]
        | [\uD800-\uDBFF] [\uDC00-\uDFFF] ;
LetterOrDigit ::= [a-zA-Z0-9$_] | ~[\u0000-\u00FF\uD800-\uDBFF]
             | [\uD800-\uDBFF] [\uDC00-\uDFFF] ;

Comment ::= '/*' .*? '*/' -> skip;
Line_Comment ::= '//' ~[\r\n]* -> skip;
StrLiteral ::= '"' ( ESCQUOTE | ~('\n'|'\r') )*? '"';

```

## Semantic Behavior

### Variable Binding

PyC supports dynamic variable binding, similar to Python. Variables can hold strings or numbers, and reassignment between types is allowed. Example:

```pyc
x = 5
x = "Hello World"
```

Chained assignments are also supported:

```pyc
x = y = 5  // Both x and y hold 5
x = 10
y = z = x  // y and z now hold 10
```

### Scoping

All variables in PyC are global. This simplifies implementation but requires careful variable management. Functions do not return values, as all variables are globally accessible.

### Control Structures

PyC follows Python-like control structures with C-style syntax:

1.  **Sequential Execution**: Code runs line-by-line from top to bottom.
    
2.  **Selection (Conditional Statements)**:
    
    -   `if` statements evaluate boolean expressions:
        
        ```pyc
        if (True) { some code }
        ```
        
3.  **Iteration (Loops)**:
    
    -   `while` loops execute based on boolean expressions:
        
        ```pyc
        while (True) { some code }
        ```
        
4.  **Functions**:
    
    -   Functions are defined using `def`:
        
        ```pyc
        def myFunction() { some code }
        ```
        

PyC retains Python’s flexibility while using C’s syntax for clarity and structure.

## Examples

1. Looping through functions
```
x=y=5
z=0

def add {
z = x + y
x = z
}

while(z > 30) {
add()
print(z)
}
```
2. Roll Routine
``` ```

3. Counting (Requires User Input)
``` ```
