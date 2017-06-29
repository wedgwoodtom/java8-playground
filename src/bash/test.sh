#!/usr/bin/env bash



haveFun()
{
    for i in {1..10}
    do
        START=$(date +%s)
        END=$(date +%s)
        DIFF=$(($END - $START))
        if [ "${DIFF}" -gt "10" ]; then
            echo "${i} took longer than 10 seconds (${DIFF})"
        fi

        echo "${i} ${DIFF}"



        CUR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
        echo "${CUR_DIR}"


    done
}


parseAndWriteFile()
{
    CLUSTER_HOSTS="host1,host2,host3"

    for hostname in $(echo ${CLUSTER_HOSTS} | sed "s/,/ /g")
    do
        # call your procedure/other scripts here below
        echo "${hostname}"
        SERVER_ENTRIES="${SERVER_ENTRIES}
        <server>
        <id>0</id>
        <host>${hostname}</host>
        <http-port>12202</http-port>
        <socket-port>12204</socket-port>
        <admin-port>12205</admin-port>
        <partitions>0, 1, 3, 7, 9, 10, 12, 20, 24, 29</partitions>
        </server>
        "
    done

    echo "${SERVER_ENTRIES}"

    # write to file
    #mkdir "config"
    FILE="./config/file.out"
    echo "${SERVER_ENTRIES}" > ${FILE}
}


getUrlContent()
{
    # $() runs the command inside the parenthesis in a subshell, which is then stored in the variable

    MY_URL="http://google.com/index.html"

    CONTENT=$(curl -L $MY_URL)

    echo "${CONTENT}"
}

haveFun
parseAndWriteFile
getUrlContent