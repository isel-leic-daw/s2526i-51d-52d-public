import {write} from "./writer.js"

function mul(a:number, b:number) {
    return a * b
}

const label = "File MUL v3"

write(label, "11 * 13 = " + mul(11,13))
write(label, "11 * 'isel' = " + mul(11, 'isel'))
write(label, "7 * 5 = " + mul(7,5))
