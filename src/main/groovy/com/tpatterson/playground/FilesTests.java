package com.tpatterson.playground;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FilesTests
{
    @Test
    public void testListFiles()
    {
        File[] hiddenFiles = new File(".").listFiles(File::isHidden);

        List<File> files = Arrays.asList(hiddenFiles);
        Assert.assertTrue(files.size()>1);
    }


}
