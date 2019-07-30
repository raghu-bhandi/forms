export abstract class DataType {
    /**
     * 
     * @param value null if this value fails validation. non-null of the right type if it passes validation
     */
    public abstract validate(value: string): any;
}

export class TextType extends DataType {
    private minLength: number;
    private maxLength: number;
    private regex: RegExp;

    public constructor(minLen: number, maxLen: number, reg: string) {
        super();
        this.minLength = minLen;
        this.maxLength = maxLen;
        if (reg) {
            this.regex = new RegExp(reg);
        }
    }
    public validate(value: string) {
        const len = value.length;
        if (this.minLength > len) {
            return null;
        }
        if (this.maxLength > 0 && len > this.maxLength) {
            return null;
        }
        if (this.regex && !this.regex.test(value)) {
            return null;
        }
        return value;
    }
}

export class IntegerType extends DataType {
    private minValue: number;
    private maxValue: number;

    public constructor(minVal: number, maxVal: number) {
        super();
        this.minValue = minVal;
        this.maxValue = maxVal;
    }
    public validate(value: string) {
        const n = Number.parseInt(value, 10);
        if (n === NaN) {
            return null;
        }
        if (n < this.minValue || n > this.maxValue) {
            return null;
        }
        return n;
    }
}

export class DecimalType extends DataType {
    private minValue: number;
    private maxValue: number;
    private factor: number;

    public constructor(minVal: number, maxVal: number, nbrDecimals: number) {
        super();
        this.minValue = minVal;
        this.maxValue = maxVal;
        this.factor = 10;
        while (nbrDecimals > 0) {
            this.factor *= 10;
            nbrDecimals--;
        }
    }

    public validate(value: string) {
        const n = Number.parseFloat(value);
        if (n === NaN) {
            return null;
        }

        if (n < this.minValue || n > this.maxValue) {
            return null;
        }
        return Math.round(n * this.factor) / this.factor;
    }
}

export class DateType extends DataType {
    private minValue: number;
    private maxValue: number;

    public constructor(minVal: number, maxVal: number) {
        super();
        this.minValue = minVal;
        this.maxValue = maxVal;
    }

    public validate(value: string) {
        //as of now, we keep it simple parsing..
        let date = Date.parse(value);
        if(!date){
            return null;
        }

        const today = new Date();
        let y = today.getFullYear();
        let m = today.getMonth();
        let d = today.getDate();
        const maxDate = new Date(y, m, d + this.maxValue);
        const minDate = new Date(y, m, d + this.minValue);
        if(date > maxDate.getTime() || date < minDate.getTime() ){
            return null;
        }
        let parsedDate = new Date(date); 
        m = parsedDate.getMonth() + 1;
        d = parsedDate.getDate();
        let s = parsedDate.getFullYear + '-';
        if(m < 10){
            s += '0';
        }
        s += m + '-';
        if(d < 10){
            s += '0';
        }
        return s + d;
    }
}

export class BooleanType extends DataType{
    public constructor(){
        super();
    }
    public validate(value: string) {
        return value;
    }

} 

