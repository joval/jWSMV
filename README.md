jWSMV: MS-WSMV implemented in Java&trade;
=============

jWSMV is a pure-Java implementation of the MS-WSMV specification, which actually works with the out-of-the-box "winrm quickconfig" Windows remote management configuration settings. It includes a Java implementation of WinRS, the Microsoft&reg; utility that uses MS-WSMV to execute commands on remote Windows&reg; machines.

The jWSMV library provides a WS-Management data model, a SOAP stack (since WS-Management is not BP/1.1-compliant, a "hand-SOAP" implementation is required), an HTTP Connection implementation for Negotiate (NTLM) authentication, and a simple command-line facsimile of WinRS. It was implemented as part of the jOVAL&trade; project.

For more information on the project, visit http://joval.org.
