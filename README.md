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
  "task": "DETECT",

  "path": "jars",
  "input": "in.jar",
  "output": "out.jar",
  
  "renamerString": "BReversed",

  "transformers": [
    "BozarFlowTransformer"
  ]
}
```
Explanation:
```
  "task":
    - "DETECT", use to detect obfuscators
    - "TRANSFORM", use to deobfuscate
    
  "path": use if jars are in a different path
  "input": input jar name, ".jar" is optional
  "output": output jar name, ".jar" is optional
  
   "renamerString": The new name renamed objects (+ the index)
  
  "transformers": list of transformers simpleName, for deobfuscation
```

https://user-images.githubusercontent.com/59488004/197333146-89ba82b3-da07-47c8-8b26-fd73a2094044.mp4
