import _ from 'lodash'
// <=> import _ from './node_modules/lodash/index.js' // Both approaches work in node

// Define some functions to use in composition
const add = (a, b) => a + b;
const square = (x) => x * x;

// Compose functions together
const addAndSquare = _.flow([add, square]);

const result = addAndSquare(2, 3);
console.log(" addAndSquare(2 + 3) => 25" + result) // Output: 25 (because (2 + 3) ^ 2 = 25)