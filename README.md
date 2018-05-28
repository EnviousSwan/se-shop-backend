# se-shop-backend

Backend part of the Software Engineering course project - Online Store RFTMarket

Build instructions (SBT)
------------------------

To run the server locally:

    sbt run

To run all unit tests:

    sbt test
    
To run some particular spec:

    sbt testOnly *ProductHttpSpec
    
    
To run some particular use case in the spec:

    sbt testOnly *ProductHttpSpec -- -z "respond with NotFound no when such category exists" 