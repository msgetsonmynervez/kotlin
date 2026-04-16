#!/bin/sh
#
# Copyright © 2015-2021 the original authors.
# Licensed under the Apache License, Version 2.0.
#
# Gradle start up script for POSIX compatible shells (bash, ksh, sh, dash)

# Attempt to set APP_HOME
APP_HOME="${APP_HOME:-$(cd "$(dirname "$0")" && pwd)}"

# Discard cd standard output in case user's .profile uses echo
APP_HOME="$(cd "${APP_HOME}" && pwd -P)" || exit

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum
warn() { echo "$*"; } >&2
die() { echo "$*"; exit 1; } >&2

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "$( uname )" in
  CYGWIN* ) cygwin=true ;;
  Darwin* ) darwin=true ;;
  MSYS* | MINGW* ) msys=true ;;
  NONSTOP* ) nonstop=true ;;
esac

CLASSPATH="${APP_HOME}/gradle/wrapper/gradle-wrapper.jar"

# Determine the Java command to use
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD=java
    if ! command -v java >/dev/null 2>&1; then
        die "ERROR: JAVA_HOME is not set and no 'java' command could be found."
    fi
fi

# Increases the maximum file descriptors if we can.
if ! "$cygwin" && ! "$darwin" && ! "$nonstop" ; then
    case $MAX_FD in
        max*)
            MAX_FD=$( ulimit -H -n ) || warn "Could not query maximum file descriptor limit"
    esac
    case $MAX_FD in
        '' | soft) :;;
        *)
            ulimit -n "$MAX_FD" || warn "Could not set maximum file descriptor limit to $MAX_FD"
    esac
fi

# Collect all arguments for the java command
set -- \
        -classpath "$CLASSPATH" \
        org.gradle.wrapper.GradleWrapperMain \
        "$@"

exec "$JAVACMD" "$@"
