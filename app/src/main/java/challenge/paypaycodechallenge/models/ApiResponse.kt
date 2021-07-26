package challenge.paypaycodechallenge.models

import java.sql.Timestamp

//___________________________________________Abstract Generic Response______________________________

abstract class GenericResponse(val success:Boolean,val terms:String,val privacy:String,
                               val source:String,val timestamp:Long ,val error:Error)

//______________________________________________Get List of Rates___________________________________
data class GetRatesResponse(val quotes: HashMap<String,Float>) :
    GenericResponse(false,"","","",0,challenge.paypaycodechallenge.models.Error(0,""))

//______________________________________________Get List of Currencies___________________________________
data class GetCurrenciesResponse(val currencies: HashMap<String,String>) :
    GenericResponse(false,"","","",0,challenge.paypaycodechallenge.models.Error(0,""))
data class Error(val code:Int,val info:String)
