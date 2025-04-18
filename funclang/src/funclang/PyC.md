


# PyC

```
 ____        __     _____ 
|  _ \ _   _ \ \   / ____|
| |_) | | | | \ \ | |     
|  __/| |_| |  > >| |____ 
|_|    \__, | /_/  \_____|
       |___/              
```


**"PyC: The Simplicity of Python Meets the Structure of C."**

  

## Introduction

PyC is a programming language that combines the simplicity of Python with the structure of C. It was created as a lightweight, easy-to-implement language for educational purposes, focusing on global scoping and dynamic typing. By blending Python's flexibility with C's syntax, PyC offers a unique approach to programming that is both accessible and powerful. This document provides an overview of PyC's design, syntax, and features, along with examples to help you get started.

  

### Philosophy

  

PyC was designed to be an easy-to-implement language that merges the syntax of Python and C. It combines Python’s dynamic variable binding with C-style scoping, removing Python’s reliance on whitespace for structure. The goal was to create a language that is simple yet powerful, with a familiar syntax for developers. Based on the Funclang codebase. It is also strongly typed, so you have to make sure that you add similar data types.

### Influences

  

PyC draws inspiration from the following languages:

  

-  **Python**: Known for its dynamic typing and ease of use, Python influenced PyC's flexible variable binding and dynamic type system.

-  **C**: PyC adopts C-style curly brace `{}` scoping to define code blocks, eliminating Python's reliance on indentation for structure.

-  **Funclang**: As the foundational codebase, Funclang provided the structural and conceptual groundwork for PyC's development.

  

## EBNF Description


```
program ::= (definedecl | statement)* ;

statement ::= funcexp | exp ;

exp ::= varexp
		| boolexp
		| funcexp
		| num_or_str
		| comments
		| compexp
		| Identifier '=' exp
		| arithexp
		| printexp
		| whileexp
		| ifexp
		| logexp
		| randomexp
		| inputexp ;


func ::= 'def' Identifier '()' '{' (exp)* '}' ;
		| Identifier '()'

arithexp ::= term
		| arithexp '+' term
		| arithexp '-' term
		| exponent '++'
		| exponent '--' ;

term ::= factor
		| term '*' factor
		| term '/' factor
		| term '%' factor ;

factor ::= exponent ;

exponent ::= numexp
		| varexp
		| '(' exp ')' ;

compexp ::= num_or_str
		| compexp '==' num_or_str
		| compexp '>' num_or_str
		| compexp '<' num_or_str
		| compexp '!=' num_or_str
		| compexp '<=' num_or_str
		| compexp '>=' num_or_str ;

num_or_str ::= num=numexp
   		| str=strexp
    	| v=varexp
    	| bl=boolexp
    	| '(' e=exp ')'
    	| a=arithexp

logexp ::= logexp ('&&' | 'and') compexp
			| logexp ('||' | 'or') compexp
			| compexp ;

printexp ::= 'print' '(' exp (',' exp)* ')' ;

whileexp ::= 'while' '(' exp ')' '{' exp* '}' ;

ifexp ::= 'if' '(' exp ')' '{' exp* '}' ('else' '{' exp* '}')? ;

randomexp ::= 'random' '(' exp ',' exp ')' ;

inputexp ::= 'input' '(' strexp ')'
		| 'input' '()' ;

comments ::= Line_Comment
		| Multi_Line_Comment ;

boolexp ::= 'true'
		| 'false' ;

numexp ::= Number
		| '-' Number
		| Number '.' Number
		| '-' Number '.' Number ;

varexp ::= Identifier ;

strexp ::= StrLiteral ;

// Lexical rules

Define ::= 'def' ;
Dot ::= '.' ;
If ::= 'if' ;
Else ::= 'else' ;
While ::= 'while' ;
Print ::= 'print' ;
TrueLiteral ::= 'true' ;
FalseLiteral ::= 'false' ;
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

## Semantics and Syntactic Behavior

  

PyC's semantic behavior is designed to balance simplicity and flexibility, making it accessible for developers while maintaining expressive power. The language enforces dynamic typing, allowing variables to change types during execution, and all variables are globally scoped to simplify implementation. Control structures such as conditionals and loops follow predictable, Python-like semantics, while functions operate without return values, relying on global state for data sharing. Error handling is minimalistic but effective, providing clear feedback for syntax, type, and runtime errors. These design choices aim to create a straightforward yet functional programming experience.

### Keywords in PyC

PyC uses a set of reserved keywords that have specific meanings and cannot be used as variable names. These keywords define the structure and behavior of the language.

|Keyword | Meaning|
|--------|--------|
| `def` | Used to define a void function. Example: `def myFunction { ... }`.|
| `if` | Starts a conditional statement. Example: `if (x > 5) { ... }`.|
| `else` | Specifies the alternative block for an `if` statement. Example: `else { ... }`.| 
| `while` | Starts a loop that executes while a condition is true. Example: `while (x < 10) { ... }`.|
| `print` | Outputs values to the console. Example: `print("Hello, World!")`.|
| `true` | Represents the boolean value `true`. Example: `x = true`.|
| `false` | Represents the boolean value `false`. Example: `x = false`.|
| `random` | Generates a random number within a range. Example: `x = random(1, 100)`.|
| `input` | Accepts user input. Example: `name = input("Enter your name: ")`.|
| `and` or `&&` | Logical AND operator. Example: `if (x > 5 and y < 10) { ... }`.| 
| `or` or `||` | Logical OR operator. Example: `if (x > 5 or y < 10) { ... }`.|
| `+` | Addition operator. Example: `32 + 8`.|
| `-` | Subtractoin operator. Example: `32 - 8`.|
| `%` | Modulus operator. Example: `32 % 3`.|
| `/` | Division operator. Example: `32 / 3`.|
| `*` | Multiply operator. Example: `32 * 3`.|
| `++` | Incrementor operator. Example: `a++`.|
| `--` | Deincrementor operator. Example: `a--`.|
| `=` | Assignment operator. Example: `a=32`.|
| `==` | Compare If-Equal operator. Example: `43==43`.|
| `!=` | Compare If-Not-Equal operator. Example: `43!=42`.|
| `<=` | Compare If-Less-Than-Or-Equal operator. Example: `34<=43`.|
| `>=` | Compare If-Greater-Than-Or-Equal operator. Example: `58>=43`.|
| `<` | Compare If-Less-Than operator. Example: `53<100`.|
| `>` | Compare If-Greater-Than operator. Example: `53>100`.|

### Variable Binding

  

PyC supports dynamic variable binding, similar to Python. Variables can hold strings or numbers, and reassignment between types is allowed. Example:

  

```pyc
x = 5
x = "Hello World"
```

  

Chained assignments are also supported:

  

```pyc
x = y = 5 // Both x and y hold 5
x = 10
y = z = x // y and z now hold 10
```

  

### Scoping

  

All variables in PyC are global. This simplifies implementation but requires careful variable management. Functions do not return values, as all variables are globally accessible. Scoping is indicated as `{...}`.

  

### Control Structures

  

PyC follows Python-like control structures with C-style syntax:

  

1.  **Sequential Execution**: Code runs line-by-line from top to bottom.

2.  **Selection (Conditional Statements)**:

-  `if` statements evaluate boolean expressions:

```pyc
if (x > 5) { print(x) }
else { print("x is too small") }
```

3.  **Iteration (Loops)**:

-  `while` loops execute based on boolean expressions:

```pyc
while (x < 10) { x++ }
```

4.  **Functions**:

- Functions are defined using `def`:

```pyc
def myFunction {
	print("Hello, World!")
}
```

  

5.  **Logical Expressions**:

- Logical operators `and` (`&&`) and `or` (`||`) are supported:

```pyc
x = 10
y = 0

if (x > 5 && y < 10) { 
	print("Both conditions are true") 
}
```

  

6.  **Random Numbers**:

- Generate random numbers within a range:

```pyc
x = random(1, 100)
print(x)
```

  

7.  **Input**:

- Accept user input with an optional prompt:

```pyc
name = input("Enter your name: ")
print("Hello,", name)
```

  ---

### Program Examples

  

#### 1. Looping through Functions

  

```pyc
x = y = 5
z = 0

def add {
	z = x + y
	x = z
}

while (z < 30) {
	add()
	print(z)
}
```

  

#### 2. Roll Routine

  

```pyc
// Simulate rolling a dice
roll = random(1, 6)
print("You rolled a", roll)
```

  

#### 3. Counting with User Input

  

```pyc
count = input("Enter a number to count to: ")
i = 0

while (i < count) {
	print(i)
	i++
}
```

  

#### 4. Logical Expressions

  

```pyc
x = 10
y = 5

if (x > y && y > 0) {
	print("x is greater than y and y is positive")
}
```

  

#### 5. Increment and Decrement

  

```pyc
x = 5
x++ // Increment x by 1
print(x)
x-- // Decrement x by 1
print(x)
```

#### 6. If Conditions

  

```pyc
x = 15
y = 10

if (x > y) {
	print("x is greater than y")
} else {
	print("x is less than y")
}
```

  

#### 7. Single-Line Comments

  

Single-line comments start with `//`:

  

```pyc
// This is a single-line comment
x = 5 // You can also place comments at the end of a line
```


#### 8. Possible Assignments

  

PyC allows flexible assignments, including:

  

1.  **Simple Assignment**:

Assign a value to a variable:

```pyc
x = 10
y = "Hello"
z = true
a = false
```

  

2.  **Chained Assignment**:

Assign the same value to multiple variables:

```pyc
x = y = z = 5
```

  

3.  **Reassignment**:

Change the value or type of a variable:

```pyc
x = 10
x = "Now I'm a string"
y = 10
z = y = x
```

  

4.  **Expression Assignment**:

Assign the result of an expression to a variable:

```pyc
x = 5 + 3
y = x * 2
```

  

5.  **Function Assignment**:

Assign the result of a built-in function call:

```pyc
result = (random(1, 100))
//returns a number from 1 to 100
```

  

6.  **Input Assignment**:

Assign user input to a variable:

```pyc
name = input("Enter your name: ")
print(name)
```

  

7.  **Boolean Assignment**:

Assign a boolean value to a variable:

```pyc
is_valid = true
is_greater = x > y

//is_greater will be assigned a boolean value of 'true'.
```

  

#### 9. Printing

  

PyC supports printing values to the console using the `print` function. You can print multiple values separated by commas, and they will be displayed with a space in between.

  

**Examples**:

  

1.  **Printing a Single Value**:

```pyc
print("Hello, World!")
```

  

2.  **Printing Multiple Values**:

```pyc
x = 10
y = "apples"
print("I have", x, y)
// Output: I have 10 apples
```

  

3.  **Printing Expressions**:

```pyc
x = 5
y = 10
print("The sum of x and y is", x + y)
// Output: The sum of x and y is 15
```

  

4.  **Printing Variables and Strings**:

```pyc
name = "Alice"
print("Hello,", name)
// Output: Hello, Alice
```

  

5.  **Printing a new line**:

```pyc
print("Hello")
print("") // Outputs a blank line
print("World")
```

#### 10. Nested While Loops and If-Statements

  

PyC supports nesting of `while` loops and `if` statements, allowing for complex control flow.

  

**Example**:

  

```pyc
x = 0
y = 0

while (x < 3) {
	y = 0
	while (y < 3) {
		if (x == y) {
			print("x equals y at", x)
		} 
		else {
			print("x:", x, "y:", y)
		}
		y++
	}
	x++
}

```

**Output**:

```

x equals y at 0

x: 0 y: 1
x: 0 y: 2
x: 1 y: 0
x equals y at 1
x: 1 y: 2
x: 2 y: 0
x: 2 y: 1
x equals y at 2
```
---
### Error Handling

  

PyC employs a simple error-handling mechanism to ensure that runtime issues are reported clearly. Errors are not recoverable within the program, and execution halts most of the time when an error is encountered. This approach simplifies the implementation while providing meaningful feedback to the developer.

  

#### Types of Errors

  

1. **Syntax Errors**:

- Detected during parsing when the code does not conform to the EBNF grammar. Usually handled by ANTLR.

  ---

2.  **Type Errors**:

- Occur when operations are performed on incompatible types.

- Example:

```pyc
x = 5 + "Hello"
// Error: Cannot add a number and a string
```

  ---

3.  **Undefined Variable Errors**:

- Raised when a variable is used before being assigned a value.

- Example:

```pyc
print(y)
// Error: Variable 'y' is not defined
```

  ---

4.  **Runtime Errors**:

- Triggered during execution, such as division by zero.

- Example:

```pyc
x = 10 / 0
// Error: Division by zero
```

  
---
#### Error Reporting

  

Errors are reported with a message indicating the type of error, the line number, and a brief description of the issue. This helps developers quickly identify and fix problems in their code.

  

#### Example of Error Output

  

```pyc
y = "Hello"
x = 5 + y
```

  

**Output**:

```
Error: Addition operation requires numeric operands.
Expression: VarExp
Variable: y
Value: "Hello" (type: StringVal)
Hint: Ensure all operands are numbers.
```

  

By keeping error handling straightforward, PyC ensures that developers can focus on writing and debugging their code without dealing with complex exception mechanisms.



  

