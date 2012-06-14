package com.hagreve.android.lib;
/*    Copyright 2012 João Neves, Carlos Fonseca, Filipe Cabecinhas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Represents a Strike as a Parcelable object (for communication through Intents)
 * @author João Neves <sevenjp@gmail.com>
 *
 */
public class Strike implements Parcelable {
	
	private String description;
	private Date start_date;
	private Date end_date;
	
	@SerializedName("source_link")
	private String source_url;
	private boolean all_day;
	private boolean canceled;
	private Submitter submitter;
	private Company company;

	// Required to correctly format dates for parceling
	private SimpleDateFormat date_fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	private static final String LOG_TAG = "HaGreveLibStrike";
	
	public static final Parcelable.Creator<Strike> CREATOR = 
		new Parcelable.Creator<Strike>() {

			public Strike createFromParcel(Parcel parcel) {
				return new Strike(parcel);
			}
			

			public Strike[] newArray(int size) {
				return new Strike[size];
			}
		};
	
	public Strike() {}

	public Strike(Parcel parcel) {
		description = parcel.readString();
		try {
			start_date = date_fmt.parse(parcel.readString());
			end_date = date_fmt.parse(parcel.readString());
		} catch (ParseException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		source_url = parcel.readString();
		all_day = parcel.readInt() != 0;
		canceled = parcel.readInt() != 0;
		submitter = new Submitter(parcel.readString());
		company = new Company(parcel.readString());
		
	}

	public String getDescription() {
		return description;
	}
	
	public String getStartDateString() {
		return date_fmt.format(start_date);
	}

	public Date getStartDate() {
		return start_date;
	}
	
	public void setStartDate(Date new_date) {
		start_date = new_date;
	}
	
	public String getEndDateString() {
		return date_fmt.format(end_date);
	}

	public Date getEndDate() {
		return end_date;
	}

	public String getSourceUrl() {
		return source_url;
	}

	public boolean isAllDay() {
		return all_day;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public String getSubmitterName() {
		return submitter.getFullName();
	}

	public String getCompanyName() {
		return company.getName();
	};
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(description);
		parcel.writeString(getStartDateString());
		parcel.writeString(getEndDateString());
		parcel.writeString(source_url);
		parcel.writeInt(all_day ? 1 : 0);
		parcel.writeInt(canceled ? 1 : 0);
		parcel.writeString(submitter.getFullName());
		parcel.writeString(company.getName());
	}

}

/**
 * Internal representation of a strike submitter
 * @author João Neves <sevenjp@gmail.com>
 *
 */
class Submitter {
	private String first_name;
	private String last_name;
	
	public Submitter() {}
	
	public Submitter(String full_name) {
		String[] tokens = full_name.split(" ");
		if(tokens.length > 0) {
			first_name = tokens[0];
			if(tokens.length > 1)
				last_name = tokens[1];
			else
				last_name = "";
		} else {
			first_name = "";
			last_name = "";
		}
	}

	public String getFirstName() {
		return first_name;
	}

	public String getLastName() {
		return last_name;
	};
	
	public String getFullName() {
		return first_name + " " + last_name;
	}
}

/**
 * Internal representation of a company on strike
 * @author João Neves <sevenjp@gmail.com>
 *
 */
class Company {
	private String name;
	
	public Company() {};
	
	public Company(String cname) {
		name = cname;
	}
	
	public String getName() {
		return name;
	}
}