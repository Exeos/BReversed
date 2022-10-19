# BReversed
A java bytecode deobfuscator

**Config**
> Creating the config:
>  
> 1. Create a file called "config.json".
> 2. Add the following template.
> 
Template:
```
{
  "task": "DETECT or TRANSFORM",

  "path": "optional, use if if jars are in a designated folder",
  "input": "JARNAME (.jar is optional)",
  "output": "JARNAME (.jar is optional)",

  "transformers": [
    "Add transformers by entering its SimpleName here for example: ZelixFlowTransformer"
  ]
}
```
