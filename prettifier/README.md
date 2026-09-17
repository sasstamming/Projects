# Itinerary Prettifier ✈️

A small command-line tool that turns admin-formatted flight itineraries into something a customer can actually read.

> **For the easiest user experience, run `java Prettifier.java -i` for a walkthrough of the process.**

## What it does

Travel agents at *Anywhere Holidays* receive raw flight itineraries from their booking portal. Those itineraries are full of internal shorthand — airport codes, city codes, and ISO-8601 timestamps — that are easy for the system to produce but hard for a customer to read. This tool runs over the text and replaces all that shorthand with readable equivalents.

### Example transformations

| Input | Becomes |
|---|---|
| `#LAX` | `Los Angeles International Airport` |
| `##EGLL` | `London Heathrow Airport` |
| `*#LAX` | `Los Angeles` |
| `*##EGLL` | `London` |
| `D(2007-04-05T12:30-02:00)` | `05 Apr 2007` |
| `T12(2007-04-05T12:30-02:00)` | `12:30PM (-02:00)` |
| `T24(2007-04-05T18:45Z)` | `18:45 (+00:00)` |

If a code isn't in the lookup CSV, or a date is malformed, it's left untouched.

## Requirements

Java 11 or higher. No compile step needed — `java` runs the `.java` file directly.

## Usage

### Standard mode

```
java Prettifier.java <input.txt> <output.txt> <airport-lookup.csv>
```

Example:

```
java Prettifier.java ./input.txt ./out.txt ./airport-lookup.csv
```

### Interactive mode (recommended for first-time users)

```
java Prettifier.java -i
```

Uses default paths (`./input.txt`, `./out.txt`, `./airport-lookup.csv`). Shows you what it's about to do, asks for confirmation, then runs with progress feedback. The output file is still written normally.

### Help

```
java Prettifier.java -h
```

## Expected file layout

```
prettifier/
├── Prettifier.java       # the program
├── airport-lookup.csv    # airport database
├── input.txt             # your itinerary (you provide)
├── out.txt               # generated output (created by the program)
└── README.md             # this file
```

## CSV format

The airport lookup CSV must have these columns: `name`, `iso_country`, `municipality`, `icao_code`, `iata_code`, `coordinates`. The first row is the header.

- Column order doesn't matter — the program reads positions from the header.
- Any missing column or blank cell will be flagged as malformed.
- Cells containing commas inside double quotes are handled correctly.

## Error messages

| Situation | Output |
|---|---|
| Wrong number of arguments | usage text |
| `-h` flag | usage text |
| Input file missing | `Input not found` |
| Lookup CSV missing | `Airport lookup not found` |
| Lookup CSV malformed (missing columns, blank cells, wrong row width) | `Airport lookup malformed` |

In any error case, the output file is not created or overwritten.

## Bonus features

- **Interactive mode** (`-i`) — friendly walkthrough with confirmation prompt and progress feedback. Built as a separate frontend on top of the same processing backend, so the default behavior is untouched.
- **Colored change summary** — at the end of a standard run, every replacement is listed with color coding (cyan for dates, blue for times, yellow for airport names, green for city names). Colors appear in the terminal only; the output file stays plain text.
- **Dynamic CSV column order** — the program reads which column is which from the header row, so the CSV can be in any order.
- **Robust CSV parsing** — handles cells that contain commas inside double quotes (e.g. `"Washington, D.C."`).
- **Z-time normalization** — ISO `Z` offsets are displayed as `(+00:00)` per spec.

## Architecture

The program is built as a pipeline: data flows through named stages, each with a single responsibility.

```
args → validate → check files exist → load lookup → read text → normalize → replace codes → replace dates → write output
```

Each stage is its own method. `main` reads top-to-bottom like a table of contents — anyone can see what the program does without reading any of the implementation. Validation lives at the top so by the time real work starts, the inputs are already trusted. Adding `-i` interactive mode was a matter of writing a second frontend that calls the same backend methods — no transformation logic had to change.
