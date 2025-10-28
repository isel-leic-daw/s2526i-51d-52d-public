import {write} from "./writer.js"

function add(a, b) {
    return a + b
}

const pIsel = document.getElementById("pIsel")
console.log(pIsel) // output: null => pq?

const label = "File add v2"

write(label, "11 + 13 = " + add(11,13))
write(label, "7 + 5 = " + add(7,5))
