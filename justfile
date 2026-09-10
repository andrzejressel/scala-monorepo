set windows-shell := ["pwsh.exe", "-c"]

fmt:
    ./mill "mill.scalalib.scalafmt/" + "__.fix"

test:
    ./mill __.test

intellij:
    ./mill mill.idea/

compile:
    ./mill __.compile

compile-ci $CI="1":
    ./mill __.compile

docs-serve:
    uv run zensical serve

docs-build:
    uv run zensical build
