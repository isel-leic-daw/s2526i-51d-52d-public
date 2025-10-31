import {write} from "./writer.js"

function mul(a: number, b: number) {
    return a * b
}

const label = "File MUL v1"

write(label, "11 * 13 = " + mul(11,13))
write(label, "7 * 5 = " + mul(7,5))
