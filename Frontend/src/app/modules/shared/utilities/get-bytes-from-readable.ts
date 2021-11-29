// e.g. 10MB = 10485760
export function getBytesFromReadable(readable: string): number {
    let powers = {'K': 1, 'M': 2, 'G': 3};
    let regex = /(\d+(?:\.\d+)?)\s?(k|m|g)?b/i;

    let result = regex.exec(readable);

    let number = Number(result[1])
    let powerSI = result[2];

    return number * Math.pow(1024, powers[powerSI]);
}