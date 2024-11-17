import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Report.css";

function Report() {
  const [wordFrequency, setWordFrequency] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null); 
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserIdAndReport = async () => {
      const token = localStorage.getItem("token");

      if (!token) {
        navigate("/login");
        return;
      }

      try {
        const profileResponse = await axios.get("http://localhost:8080/api/users/profile", {
          headers: { Authorization: `Bearer ${token}` },
        });

        const userId = profileResponse.data.id;
        console.log("User ID:", userId); 

        const reportResponse = await axios.get(`http://localhost:8080/api/blogs/report/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        console.log("Report Response:", reportResponse); 

        if (reportResponse.status === 200) {
          setWordFrequency(Object.entries(reportResponse.data)); 
        } else {
          setError("No data available for analysis.");
        }
      } catch (error) {
        console.error("Error fetching report or user profile:", error);
        setError("Failed to load the report. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    fetchUserIdAndReport();
  }, [navigate]);

  if (loading) {
    return <p>Loading report...</p>;
  }

  if (error) {
    return <p className="error">{error}</p>;
  }

  return (
    <div className="report-container">
      <h1>Report</h1>
      <div className="report-card">
        <h3>Top 5 Most Frequent Words</h3>
        {wordFrequency.length > 0 ? (
          <ul>
            {wordFrequency.map(([word, count], index) => (
              <li key={index}>
                <strong>{word}</strong>: {count} occurrences
              </li>
            ))}
          </ul>
        ) : (
          <p>No data available for analysis.</p>
        )}
      </div>
      <button className="back-button" onClick={() => navigate("/dashboard")}>
        Back to Dashboard
      </button>
    </div>
  );
}

export default Report;
