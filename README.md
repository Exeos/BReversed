# BReversed
A java bytecode deobfuscator

[INFO]
To use BReversed you have to add a config file.
You must call the file "config.json", the format is following:

{
  "task": "DETECT or TRANSFORM",

  "path": "optional, use if if jars are in a designated folder",
  "input": "JARNAME (.jar is optional)",
  "output": "JARNAME (.jar is optional)",

  "transformers": [
    "Add transformers by entering its SimpleName here for example: ZelixFlowTransformer"
  ]
}
