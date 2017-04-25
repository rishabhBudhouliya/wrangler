# Type Functions

These are functions for detecting the type of data. These functions can be used in the
directives `filter-row-if-false`, `filter-row-if-true`, `filter-row-on`, or
`send-to-error`.

## Pre-requisite
These can be used only in the `filter-*` or `send-to-error` directives.

## Namespace

All type-related functions are in the namespace
```
  type
```

## Example Data

Upload to the workspace `body` an input record such as:

```
{
    "name": {
        "fname": "Joltie",
        "lname": "Root",
        "mname": null
    },
    "date": "12/17/2019",
    "time": "10:45 PM",
    "boolean": "true",
    "coordinates": [
        12.56,
        45.789
    ],
    "numbers": [
        1,
        2.1,
        3,
        null,
        4,
        5,
        6,
        null
    ],
    "moves": [
        { "a": 1, "b": "X", "c": 2.8},
        { "a": 2, "b": "Y", "c": 232342.8},
        { "a": 3, "b": "Z", "c": null},
        { "a": 4, "b": "U"}
    ],
    "integer": "1",
    "double": "2.8",
    "empty": "",
    "float": 45.6,
    "aliases": [
        "root",
        "joltie",
        "bunny",
        null
    ]
}
```

Once such a record is loaded, apply these directives before applying any of the functions listed here:
```
  parse-as-json body
  columns-replace s/body_//g
```

## List of Type Functions

| Function | Description | Example |
| :------- | :---------- | :------ |
|isDate(string)| Checks if the string value is a date field. True if it is, false otherwise. | `filter-row-if-true type:isDate(date)` |
|isTime(string)| Checks if the string value is a date time. True if it is, false otherwise. | `filter-row-if-true type:isTime(time)` |
|isBoolean(string| Checks if the string value is a booelan field. True if it is, false otherwise. | `send-to-error !type:isBoolean(boolean)` |
|isNumber(string| Checks if the string value is a number field. True if it is, false otherwise. | `send-to-error !type:isNumber(integer)` |
|isEmpty(string| Checks if the string value is empty. True if it is, false otherwise. | `send-to-error !type:isEmpty(empty)` |
|isDouble(string| Checks if the string value is a double field. True if it is, false otherwise. | `send-to-error !type:isDouble(double)` |
|isInteger(string| Checks if the string value is an integer field. True if it is, false otherwise. | `send-to-error !type:isInteger(integer)` |