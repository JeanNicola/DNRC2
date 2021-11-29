export const formatPayload = (input: {
  [key: string]: any;
}): { [key: string]: any } => {
  const output = { ...input };
  delete output.editMode;
  delete output.highlight;
  return output;
};
