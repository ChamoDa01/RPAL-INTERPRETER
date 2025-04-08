# RPAL Compiler

An RPAL (Right-reference Pure Applicative Language) Compiler implemented in Java — built as part of a Programming Languages coursework/project.

This compiler takes RPAL source code as input and produces an Abstract Syntax Tree (AST), a Standardized Tree (ST), and further generates control structures for the CSE (Control Stack Environment) Machine.

---

## Table of Contents

- [About RPAL](#about-rpal)
- [Features](#features)
- [Architecture](#architecture)
- [Grammar Rules](#grammar-rules)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Build & Run](#build--run)
- [Usage](#usage)
- [Example](#example)
- [Error Handling](#error-handling)
- [Contributors](#contributors)

---

## About RPAL

RPAL is a purely functional programming language designed for teaching compiler design and functional programming concepts.  
It supports:

- Lambda Calculus Constructs
- Function Definitions
- Conditional Expressions
- Recursive Bindings
- List Manipulation
- Operators (arithmetic, boolean, comparison)

---

## Features

- Lexical Analysis → Token Generation
- Syntax Analysis → Parse Tree & Abstract Syntax Tree (AST) Construction
- Tree Standardization → Standard Tree (ST) Creation
- CSE Machine Code Generation → Control Structures
- Error Handling → Descriptive Parse Errors
- Supports:
  - Operators: +, -, *, /, >=, <=, =, >, <, etc.
  - Keywords: let, in, fn, where, within, etc.
  - Function Definitions
  - Recursive Constructs
  - Augmentations
  - Conditionals
  - Logical Operators: and, or, not
  - List Handling

---

## Architecture

