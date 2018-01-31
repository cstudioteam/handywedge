ソフトウェア構成
===================

**Handywedge** を用いたソフトウェア構成を :numref:`tab-software-structure` に示す。

.. table:: ソフトウェア構成図
   :name: tab-software-structure

   +---------------------------------------------------------------------+
   |                           Application                               |
   +================+=======+============================================+
   |  UI Component  |  CDI  |      **Handywedge**                        |
   +----------------+-------+---+--------+--------------+----------------+
   |                            |        |              | Logging Facade |
   +    (Servlet Container)     +  JDBC  +  PDF Engine  +----------------+
   |                            |        |              | Logger         |
   +----------------------------+--------+--------------+----------------+
   |  Java VM                                                            |
   +---------------------------------------------------------------------+
   |  OS                                                                 |
   +---------------------------------------------------------------------+


対象ソフトウェアプロダクトを :numref:`tab-software-product` に示す。

.. csv-table:: 対象ソフトウェアプロダクト
   :name: tab-software-product
   :header: "ソフトウェア ", "プロダクト", "備　考"
   :widths: 20 25 55

   "OS"
   "Java VM", "Java 8以上"
   "AP Server", "Java EE7以上", "Tomcat 8など"
   "JDBC", "JDBC 4.0以上", "PostgreSQL 9、SQLServer、Oracleなど"
   "PDF Engine", "JasperReport 6.3.0"
   "Logger", "", "log4j 2.5など"
   "Logging Facade", "slf4j 1.7.13"
   "UI Component", "", "PrimeFaces 6など"
   "CDI", "Weld 2.2.4 Final"


.. attention:: 各ソフトウェアのバージョンは互換性を確認した上で変更されることがある。

