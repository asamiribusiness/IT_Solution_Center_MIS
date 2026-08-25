/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itsolutioncenter.util;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptTest {
    public static void main(String[] args) {
        String plainPassword = "12345";
      
        // Test 1: Hash and verify
        System.out.println("=== Test 1: Hash and Verify ===");
        String hash = BCrypt.hashpw("12345", BCrypt.gensalt());
        System.out.println("Generated hash: " + hash);
        System.out.println("Verification: " + BCrypt.checkpw(plainPassword, hash));
       
        // Test 2: Verify against known hash
        System.out.println("\n=== Test 2: Known Hash Test ===");
        String knownHash = "$2a$10$abcdefghijklmnopqrstuvwxyz123456";
        String testPassword = "myPassword";
        // This should fail unless you use the matching password
       
        // Test 3: Manual verification
        System.out.println("\n=== Test 3: Manual Verification ===");
        String testHash = BCrypt.hashpw("secret", BCrypt.gensalt());
        System.out.println("Hash for 'secret': " + testHash);
        System.out.println("Check 'secret': " + BCrypt.checkpw("secret", testHash));
        System.out.println("Check 'wrong': " + BCrypt.checkpw("wrong", testHash));
    }
}