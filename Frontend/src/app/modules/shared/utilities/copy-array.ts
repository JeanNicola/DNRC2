export function copyArray(arr : object[]) {
    const newArray = [];
    arr.forEach(val => newArray.push(Object.assign({}, val)));
    return newArray;
}