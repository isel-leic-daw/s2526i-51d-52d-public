/*
TypeScript:
- Created by Microsoft
- Adds type information on top JavaScript
  - Type checking during development time
    - E.g. IDE
*/

// = Primitive types

let aNumber: number
aNumber = 1
// ERROR aNumber = "1"
let aString: string
aString = "1"
// ERROR aString = 1
aString.toUpperCase()
// ERROR aString / aString
// ERROr aNumber.toUpperCase()
let aBoolean: boolean = true

function add(x: number, y: number)/* inferred as number */ {
  return x + y
}
add(1, 2)
// ERROR add(1, "2")
// ERROR const anotherString: string = add(1,2)

// = array types
const anArray: Array<string> = ["hello", "world"]
anArray[0].toUpperCase()
const anArrayOfArray: Array<Array<string>> = [["hello", "world"], ["olá", "mundo"]]

// = tuples
const aNumberPair: [number, number] = [1, 2]
const aNumberAndStringPair: [number, string] = [1, "olá"]
const aTriple: [number, string, boolean] = [1, "hello", true]

// = function types
let isOdd: (input: number) => boolean
isOdd = function(input) {
  return input % 2 == 1
}

// ERROR isOdd = (n) => n + "ola"

// = union types
let numberOrString: number | string
numberOrString = "hello"
numberOrString = 2
// ERROR numberOrString = true
function f(): number | string {
  throw new Error()
}
numberOrString = f()
// ERROR numberOrString.toUpperCase()

if(typeof numberOrString === "string") {
  // type narrowing: string
  numberOrString.toUpperCase()
} else {
  // type narrowing: number
  numberOrString / 2
}

// = object types
const alice: {name: string, nbr: number} = {
  name: "Alice",
  nbr: 12345,
}

// = type definitions
type Student = {name: string, nbr: number, course: string}
const bob: Student = {
  name: "Bob",
  nbr: 23456,
  course: "LEIC"
}

// Unions
type StringOrNumber = string | number
const someConst: StringOrNumber = "hello"

// = Structural Type System vs Nominal Type System

// type Student = {name: string, nbr: number, course: string} // defined above
type Undergraduate = {name: string, nbr: number}
const carol = { // carol has an ANONYMOUS type
  name: "Carol",
  nbr: 34566,
  course: "MEIC"
}
let mary: Undergraduate = carol
const katy: Student = carol
mary = katy
// ERROR const rose: Student = mary

type Teacher = {
  name: string,
  nbr: number,
  course?: string
}

const david: Teacher = {
  name: "David",
  nbr: 2,
}

const eleanor: Teacher = {
  name: "Eleanor",
  nbr: 2,
  course: "LEIC"
}

const foo: Undergraduate = eleanor

// = Literal types

type Red = "red"
const c1: Red = "red"
type PrimitiveColour = "red" | "green" | "blue"
const c2: PrimitiveColour = "blue"
// ERROR const c3: PrimitiveColour = "yellow"

// = Discriminated Unions

type FetchResult = 
| {type: "error", message: string, error: Error}
| {type: "response", statusCode: number}

function load(): FetchResult {
  throw new Error()
}

function handleFetchResult(result: FetchResult) {
  switch(result.type) {
    case "error":
      result.message
      result.error
      // ERROR result.statusCode
      break
    case "response":
      result.statusCode
      // ERROR result.message
      break
  }
}

handleFetchResult(load())

// = Intersection types

type HasLength = {length: number}
const x: HasLength = "some string"
const y: HasLength = [1]

type HasToUpperCase = {toUpperCase: () => string}
const x1: HasToUpperCase = "some string"
// ERROR const y1: HasToUpperCase = [1]

type HasLengthAndToUpperCase = HasLength & HasToUpperCase
const z: HasLengthAndToUpperCase = "some string"
// ERROR: const w: HasLengthAndToUpperCase = [2, 3]