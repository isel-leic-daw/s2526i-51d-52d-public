import {write} from "./writer.js"

function add(a, b) {
    return a + b
}

const label = "File add v2"

write(label, "11 + ola = " + add(11, "ola"))
write(label, "7 + 5 = " + add(7,5))
