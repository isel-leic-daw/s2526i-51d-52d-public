import _ from 'lodash'

// Define some functions to use in composition
const add = (a, b) => a + b;
const square = (x) => x * x;

// Compose functions together
const addAndSquare = _.flow([add, square]);

const result = addAndSquare(2, 3);
console.log(result) // Output: 25 (because (2 + 3) ^ 2 = 25)