# BReversed
A java bytecode deobfuscator

## Project Setup

### Prerequisites

- java 21+
- git

### Cloning Project & Dependencies

Clone BReversed and the required dependencies:

```bash
git clone https://github.com/Exeos/BReversed.git
git clone https://github.com/Exeos/ASMPlus.git
```

> **⚠️ Before you continue:** Setup [ASMPlus](https://github.com/Exeos/ASMPlus/) as described in its [README](https://github.com/Exeos/ASMPlus/blob/master/README.md)

Resulting layout:

```
/
├── BReversed/
└── ASMPlus/
```

Make a local gradle properties file from the example and update the dependency paths. From the `BReversed` directory run:

```bash
cp gradle.properties.example gradle.properties
```

Open `gradle.properties` and set the dependency paths (these are the defaults you can use if the repos are siblings as shown above):

```
dependencies.asmplus.path=../ASMPlus
```

### Building the Project

BReversed uses Gradle Composite Builds to include ASMPlus and jlib. After cloning and configuring `gradle.properties`, build from the `BReversed` directory:

Unix / macOS:
```bash
./gradlew build
```

Windows:
```bash
gradlew.bat build
```

---

## Config

BReversed is driven by a JSON configuration file. Create a `config.json` in the working directory you run BReversed from.

### Example template

```json
{
  "task": "DETECT",

  "path": "jars",
  "input": "in.jar",
  "output": "out.jar",

  "renamerString": "BReversed",

  "transformers": [
    "crasher/IMGCrasherRemover",
    "bozar/",
    "!BozarLightFlow"
  ]
}
```

### Fields explained

- `task`
    - `"DETECT"` — analyze the input and print detected obfuscators.
    - `"TRANSFORM"` — run configured transformers and write the transformed jar to `output`.

- `path`
    - Optional. Directory where jars live (relative to working directory).

- `input`
    - The input jar file name (the `.jar` extension is optional). Example: `"in.jar"` or `"in"`.

- `output`
    - The output jar file name (the `.jar` extension is optional). When `task` is `"TRANSFORM"`, the processed jar will be written here.

- `renamerString`
    - Base string used when renaming members (classes, fields, methods). Indices may be appended.

- `transformers`
    - Array of transformers to apply when `task` is `"TRANSFORM"`.
    - Format notes:
        - `"packagename/TransformerSimpleName"` — enable a single transformer.
        - `"packagename/"` — enable all transformers in that package.
        - `"!TransformerSimpleName"` — exclude a transformer (negation).
    - Order matters: transformers are applied in the listed order unless there is a Comparator for the transformer(s) parent package.

### Tips & Best Practices

- Start with `"DETECT"` to understand what protections are present before running transforms.
- Use `!Transformer` entries to exclude known-bad or unstable transformers.

---

https://user-images.githubusercontent.com/59488004/197333146-89ba82b3-da07-47c8-8b26-fd73a2094044.mp4