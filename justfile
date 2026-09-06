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

docs-watch:
    docker run --rm -itp 8080:8080 -p 3001:3001 -v ./docs/scala-monorepo:/usr/src/app/content ghcr.io/jackyzha0/quartz:sha-075afd3