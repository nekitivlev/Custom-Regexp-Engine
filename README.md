# Custom-Regexp-Engine
A custom regular expression engine that takes into account the carriage position when searching for a substring that satisfies a regular expression.

### Specification:
- You have to use a backslash (`\`) as an escape character before the characters `\`, `?`, `*`, `+`, `(`, and `)`. For instance, use `\\` when you want a backslash, and `\?` for a question mark, etc.
- All other characters are read literally.
- There's a `\c` character that specifies the caret position. This means that the `w\cord` pattern looks for instances of 'word' where the caret is positioned between `w` and `o`.
- Similar to the majority of existing patterns, any content in brackets is a group. A group supports the quantifiers `?`, `*`, and `+`, where:
  - `?` denotes 0 or 1 repetition.
  - `*` denotes 0 or more repetitions.
  - `+` denotes 1 or more repetitions.
  - Quantifiers can only be placed after the closing bracket of a group.
- A group must have content. It cannot be empty.
- Regular expressions should work using only ASCII characters. Other characters are not allowed.


#### Examples
| Input text | Caret position | Pattern | Result |
|--|--|--|--|
| `Hello world!` | Doesn't matter | `word` | `[[6, 11]]` |
| `Hello world!` | Doesn't matter | `Hello( word\!)?` | `[[0, 5], [0, 12]]` |
| `Ho-ho-ho`| Doesn't matter | `Ho(-ho)*`| `[[0, 2], [0, 5], [0, 8]]` |
| `word word word` | Caret with `offset == 3` | `\cword` | `[]` |
| `word word word` | Caret with `offset == 5` | `\cword` | `[[5, 9]]` |
| `word word word` | Caret with `offset == 0`, caret with `offset == 5` | `\cword` | `[[0, 4], [5, 9]]` |
