//package com.handywedge.o365.model;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//@Getter
//@Setter
//@ToString
//@JsonIgnoreProperties(ignoreUnknown = true)
//
//    /// <summary>
//    ///
//    /// </summary>
//    [AttributeUsage(AttributeTargets.Property | AttributeTargets.Field, AllowMultiple = false)]
//    public class PeriodRangeAttribute extends ValidationAttribute
//    {
//        /// <summary>
//        ///
//        /// </summary>
//        /// <value></value>
//        public String[] AllowableValues;
//
//        /// <summary>
//        ///
//        /// </summary>
//        /// <param name="value"></param>
//        /// <param name="validationContext"></param>
//        /// <returns></returns>
//        protected override ValidationResult IsValid(object value, ValidationContext validationContext)
//        {
//
//            if(value == null || String.IsNullOrEmpty(value.ToString()) ||
//                value.ToString() == PeriodType.Today || value.ToString() == PeriodType.Current)
//            if (this.AllowableValues?.Contains(value?.ToString()) == true)
//            {
//                return ValidationResult.Success;
//            }
//
//
//            return new ValidationResult(String.Format("Invalid paramter value. Period={0}", value));
//        }
//    }
//}