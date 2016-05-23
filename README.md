# Flupot

A ClojureScript library for creating [React][] elements, in a similar
style to [Om][]'s `om.dom` namespace.

[react]: https://facebook.github.io/react/
[om]: https://github.com/omcljs/om

## Installation

Add the following to your project `:dependencies`:

    [flupot "0.2.1"]

## Usage

Require the `flupot.dom` namespace:

```clojure
(ns flupot.example
  (:require [flupot.dom :as dom]))
```

There is a function for each DOM element:

```clojure
(dom/div (dom/p "Hello World"))
```

If the first argument is a map, it's used as the element's attributes:

```clojure
(dom/div {:class "foo"} (dom/p "Hello World"))
```

Special React options like `:key` are also supported.

If one of the child arguments is a seq, it's expanded out automatically:

```clojure
(dom/ul
 (for [i (range 5)]
   (dom/li {:key i} i)))
```

## License

Copyright © 2016 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
