/*
 * Copyright (C) 2014 Intel Corporation
 * All rights reserved.
 */

package com.intel.mtwilson.setup.cmd;

import com.intel.dcsg.cpg.io.Filename;
import com.intel.mtwilson.client.AbstractCommand;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author rksavino
 */
public class EncodeUsername extends AbstractCommand {

    @Override
    public void execute(String[] args) throws Exception {
        if( args.length < 1 ) {
            throw new IllegalArgumentException("Usage: EncodeUsername alias | EncodeUsername -  (and supply username on stdin)");
        }
        if( args[0].equals("-") ) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(Filename.encode(in.readLine()));
            in.close();
        }
        else {
            System.out.println(Filename.encode(args[0])); 
        }
    }

}
